package com.AgentInsight.service.Impl;

import com.AgentInsight.CustomException.AgentNotFoundException;
import com.AgentInsight.CustomException.EmailAlreadyExistsException;
import com.AgentInsight.CustomException.PerformanceDataException;
import com.AgentInsight.dto.AgentPerformanceDTO;
import com.AgentInsight.dto.AgentReportDTO;
import com.AgentInsight.dto.ResponceDTO.IncentiveResponseDTO;
import com.AgentInsight.dto.UserDto;
import com.AgentInsight.entity.Incentive;
import com.AgentInsight.entity.Sales;
import com.AgentInsight.entity.UserPrincipal;
import com.AgentInsight.entity.Users;
import com.AgentInsight.dto.ResponceDTO.SaleResponseDTO;

import com.AgentInsight.enums.UserRole;
import com.AgentInsight.repository.IncentiveRepository;
import com.AgentInsight.repository.SalesRepository;
import com.AgentInsight.repository.UserRepository;
import com.AgentInsight.security.JWTUtil;
import com.AgentInsight.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private IncentiveRepository incentiveRepository;

    @Autowired
    private JWTUtil jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private AuthenticationManager authManager;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting to load user by email: {}", email);
        Users user = userRepository.findByEmail(email);

        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new UserPrincipal(user);
    }

    @Override
    public void addUser(Users user){
        logger.info("Attempting to add new user with email: {}", user.getEmail());
        if(userRepository.existsByEmail(user.getEmail())){
            logger.warn("Add user failed: Email {} already exists", user.getEmail());
            throw new EmailAlreadyExistsException("An Account with email: " + user.getEmail() + " Exists");
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.AGENT);
        userRepository.save(user);
        logger.info("User {} successfully added as AGENT with encrypted password", user.getEmail());
    }

    @Override
    public List<UserDto> getAllUsers(){
        logger.debug("Fetching all users from repository");
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getAgentid(), user.getName(), user.getEmail(),
                        user.getPhone(), user.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String,String> verify(Users user) {
        logger.info("Authentication request for user: {}", user.getEmail());
        Users existingUser = userRepository.findByEmail(user.getEmail());
        Map<String,String> response = new HashMap<>();

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                logger.info("Authentication successful for user: {}", user.getEmail());
                String token = jwtService.generateToken(user.getEmail(), existingUser.getRole());
                response.put("token", token);
                response.put("message", "Login Successful");
                response.put("role",existingUser.getRole().toString());
                response.put("agentid", existingUser.getAgentid());
                response.put("agentname",existingUser.getName());
                response.put("phoneno",existingUser.getPhone());
                return response;
            }
        } catch (org.springframework.security.core.AuthenticationException e) {
            logger.error("Authentication failed for user: {}. Error: {}", user.getEmail(), e.getMessage());
            response.put("error", "Login Failed");
            return response;
        }

        logger.warn("Authentication failed: invalid credentials for user {}", user.getEmail());
        response.put("error", "Login Failed");
        return response;
    }


    @Override
    @Transactional
    public void deleteUser(String agentid) {
        logger.info("Request to delete agent ID: {}", agentid);
        if (userRepository.existsById(agentid)) {
            userRepository.deleteById(agentid);
            logger.info("Agent ID: {} deleted successfully", agentid);
        } else {
            logger.error("Delete failed: Agent ID {} not found", agentid);
            throw new AgentNotFoundException(agentid);
        }
    }


    @Override
    public UserDto getUserById(String agentid) {
        logger.debug("Fetching user details for ID: {}", agentid);
        Users user = userRepository.findById(agentid).orElseThrow(() -> {
            logger.error("User not found for ID: {}", agentid);
            return new AgentNotFoundException(agentid);
        });
        return new UserDto(user.getAgentid(), user.getName(), user.getEmail(), user.getPhone(), user.getRole());
    }

    @Override
    public Users createUser(Users user) {
        logger.info("Creating user: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    @Override
    public void updateUser(String agentid, Users updatedUser) {
        logger.info("Updating user profile for agent ID: {}", agentid);
        Users existingUser = userRepository.findById(agentid)
                .orElseThrow(() -> {
                    logger.error("Update failed: User ID {} not found", agentid);
                    return new AgentNotFoundException(agentid);
                });

        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            logger.info("Password updated and encrypted for agent ID: {}", agentid);
        }

        userRepository.save(existingUser);
        logger.info("User ID: {} updated successfully", agentid);
    }

    @Override
    public List<AgentReportDTO> getAgentReport() {
        logger.info("Generating full agent performance report");
        return userRepository.getAgentPerformanceReport();
    }

    @Override
    public AgentPerformanceDTO getAgentPerformanceById(String agentid) {
        logger.info("Generating performance report for agent ID: {}", agentid);

        try {
            Users user = userRepository.findById(agentid)
                    .orElseThrow(() -> {
                        logger.error("Performance lookup failed: Agent ID {} not found", agentid);
                        return new AgentNotFoundException(agentid);
                    });

            if (com.AgentInsight.enums.UserRole.ADMIN.equals(user.getRole())) {
                logger.warn("Performance lookup rejected: ID {} belongs to an ADMIN", agentid);
                throw new PerformanceDataException("Performance reports are not available for Admin accounts.");
            }

            List<Sales> sales = salesRepository.findByAgent_Agentid(agentid);
            if (sales == null) sales = java.util.Collections.emptyList();

            List<Incentive> incentives = incentiveRepository.findByAgent_Agentid(agentid);
            if (incentives == null) incentives = java.util.Collections.emptyList();

            List<SaleResponseDTO> saleDtos = sales.stream().map(s -> {
                SaleResponseDTO dto = new SaleResponseDTO();
                dto.setSaleid(s.getSaleid());
                dto.setAmount(s.getSaleamount() != null ? s.getSaleamount() : 0.0);
                dto.setSaletype(s.getSaletype());
                dto.setStatus(s.getStatus());
                dto.setAgentid(agentid);
                dto.setAgentName(user.getName());

                if (s.getPolicy() != null) {
                    dto.setPolicyid(s.getPolicy().getPolicyId());
                    dto.setPolicyName(s.getPolicy().getName());
                }

                if (s.getSaledate() != null) {
                    dto.setSaleDate(s.getSaledate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                }
                return dto;
            }).collect(Collectors.toList());

            List<IncentiveResponseDTO> incentiveDtos = incentives.stream().map(i -> {
                Double amount = i.getAmount() != null ? i.getAmount() : 0.0;
                Double bonus = Math.round((amount * 0.1) * 100.0) / 100.0;
                return new IncentiveResponseDTO(i.getIncentiveid(), agentid, amount, i.getCalculationdate(), i.getStatus(), user.getName(), bonus);
            }).collect(Collectors.toList());

            double totalSales = saleDtos.stream().mapToDouble(SaleResponseDTO::getAmount).sum();
            double totalIncentives = incentiveDtos.stream().mapToDouble(IncentiveResponseDTO::getAmount).sum();

            logger.info("Successfully compiled performance data for agent: {}", user.getName());
            return new AgentPerformanceDTO(user.getAgentid(), user.getName(), user.getEmail(), user.getPhone(), saleDtos, incentiveDtos, totalSales, totalIncentives);

        } catch (AgentNotFoundException | PerformanceDataException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error generating performance report for agent {}: {}", agentid, e.getMessage(), e);
            throw new PerformanceDataException("An internal error occurred while generating the report.", e);
        }
    }
}