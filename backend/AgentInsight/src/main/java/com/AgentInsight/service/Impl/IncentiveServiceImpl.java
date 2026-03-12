package com.AgentInsight.service.Impl;

import com.AgentInsight.CustomException.AgentNotFoundException;
import com.AgentInsight.CustomException.IncentiveNotFoundException;
import com.AgentInsight.CustomGenerator.IncentiveId.IncentiveIdGeneratorUtil;
import com.AgentInsight.dto.ResponceDTO.IncentiveResponseDTO;
import com.AgentInsight.dto.requestDTO.IncentiveRequestDTO;
import com.AgentInsight.entity.Incentive;
import com.AgentInsight.entity.Sales;
import com.AgentInsight.entity.Users;
import com.AgentInsight.repository.IncentiveRepository;
import com.AgentInsight.repository.UserRepository;
import com.AgentInsight.service.IncentiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncentiveServiceImpl implements IncentiveService {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveServiceImpl.class);

    @Autowired
    private IncentiveRepository incentiveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncentiveIdGeneratorUtil incentiveIdGenerator;

    @Override
    public List<IncentiveResponseDTO> getIncentives() {
        logger.debug("Fetching all incentives");
        return incentiveRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<IncentiveResponseDTO> getAllIncentivesWithDetails() {
        logger.debug("Fetching all incentives with bonus details");
        return incentiveRepository.findAll().stream()
                .map(this::convertToDtoWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public Page<IncentiveResponseDTO> getAllIncentivesWithDetails(Pageable pageable) {
        return incentiveRepository.findAll(pageable).map(this::convertToDtoWithDetails);
    }

    @Override
    public Page<IncentiveResponseDTO> getIncentives(Pageable pageable) {
        return getAllIncentivesWithDetails(pageable);
    }

    @Override
    public IncentiveResponseDTO getIncentiveByIncentiveId(String incentiveId) {
        logger.info("Fetching incentive ID: {}", incentiveId);
        return incentiveRepository.findByIncentiveid(incentiveId)
                .map(this::convertToDtoWithDetails)
                .orElseThrow(() -> {
                    logger.error("Incentive lookup failed: ID {} not found", incentiveId);
                    return new IncentiveNotFoundException(incentiveId);
                });
    }

    @Override
    public List<IncentiveResponseDTO> getIncentivesByAgentId(String agentId) {
        if (!userRepository.existsById(agentId)) {
            throw new AgentNotFoundException(agentId);
        }
        return incentiveRepository.findByAgent_Agentid(agentId).stream()
                .map(this::convertToDtoWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public Double getTotalIncentivesAmount() {
        return incentiveRepository.findAll().stream()
                .mapToDouble(i -> i.getAmount() != null ? i.getAmount() : 0.0)
                .sum();
    }

    @Override
    public Double getTotalBonusAmount() {
        return incentiveRepository.findAll().stream()
                .mapToDouble(i -> {
                    Double amount = i.getAmount() != null ? i.getAmount() : 0.0;
                    return Math.round((amount * 0.1) * 100.0) / 100.0;
                })
                .sum();
    }

    @Override
    public Integer getPendingCount() {
        return (int) incentiveRepository.findAll().stream()
                .filter(i -> "Pending".equalsIgnoreCase(i.getStatus()))
                .count();
    }

    @Override
    @Transactional
    public void calculateAndCreateIncentiveForSale(Sales sale) {
        String incentiveId = "I-" + sale.getSaleid();

        if (incentiveRepository.findByIncentiveid(incentiveId).isPresent()) {
            logger.warn("Incentive already exists for sale ID: {}", sale.getSaleid());
            return;
        }

        Incentive incentive = new Incentive();
        incentive.setIncentiveid(incentiveId);
        incentive.setAgent(sale.getAgent());
        incentive.setAmount(sale.getSaleamount() != null ? sale.getSaleamount() : 0.0);
        incentive.setCalculationdate(sale.getSaledate());
        incentive.setStatus("Pending");

        incentiveRepository.save(incentive);
        logger.info("Incentive {} created for sale {}", incentiveId, sale.getSaleid());
    }

    @Override
    public List<IncentiveResponseDTO> getAllIncentivesWithDetailsForReports() {
        return getAllIncentivesWithDetails();
    }

    @Override
    public List<IncentiveResponseDTO> getAllIncentivesWithDetailsForPerformance() {
        return getAllIncentivesWithDetails();
    }

    @Override
    @Transactional
    public IncentiveResponseDTO updateIncentiveStatus(String incentiveId, String status) {
        logger.info("Updating incentive {} status to {}", incentiveId, status);
        Incentive incentive = incentiveRepository.findByIncentiveid(incentiveId)
                .orElseThrow(() -> new IncentiveNotFoundException(incentiveId));

        incentive.setStatus(status);
        return convertToDtoWithDetails(incentiveRepository.save(incentive));
    }

    @Override
    @Transactional
    public IncentiveResponseDTO createIncentive(IncentiveRequestDTO incentiveRequest) {
        Users agent = userRepository.findById(incentiveRequest.getAgentid())
                .orElseThrow(() -> new AgentNotFoundException(incentiveRequest.getAgentid()));

        Incentive incentive = new Incentive();
        incentive.setIncentiveid(incentiveIdGenerator.generateId());
        incentive.setAgent(agent);
        incentive.setAmount(incentiveRequest.getAmount());
        incentive.setCalculationdate(incentiveRequest.getCalculationdate());
        incentive.setStatus(incentiveRequest.getStatus());

        return convertToDtoWithDetails(incentiveRepository.save(incentive));
    }

    @Override
    public IncentiveResponseDTO convertToDto(Incentive incentive) {
        String agentName = incentive.getAgent() != null ? incentive.getAgent().getName() : "Unknown Agent";
        return new IncentiveResponseDTO(
                incentive.getIncentiveid(),
                incentive.getAgent() != null ? incentive.getAgent().getAgentid() : null,
                incentive.getAmount(),
                incentive.getCalculationdate(),
                incentive.getStatus(),
                agentName,
                0.0
        );
    }

    @Override
    public IncentiveResponseDTO convertToDtoWithDetails(Incentive incentive) {
        String agentName = incentive.getAgent() != null ? incentive.getAgent().getName() : "Unknown Agent";
        Double amount = incentive.getAmount() != null ? incentive.getAmount() : 0.0;
        Double bonus = Math.round((amount * 0.1) * 100.0) / 100.0;

        return new IncentiveResponseDTO(
                incentive.getIncentiveid(),
                incentive.getAgent() != null ? incentive.getAgent().getAgentid() : null,
                amount,
                incentive.getCalculationdate(),
                incentive.getStatus(),
                agentName,
                bonus
        );
    }
}