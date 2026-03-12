package com.AgentInsight.service.Impl;

import com.AgentInsight.CustomException.AgentNotFoundException;
import com.AgentInsight.CustomException.PolicyNotFoundException;
import com.AgentInsight.dto.ResponceDTO.SaleResponseDTO;
import com.AgentInsight.dto.requestDTO.SaleRequestDto;
import com.AgentInsight.entity.Policy;
import com.AgentInsight.entity.Sales;
import com.AgentInsight.entity.Users;
import com.AgentInsight.repository.PolicyRepository;
import com.AgentInsight.repository.SalesRepository;
import com.AgentInsight.repository.UserRepository;
import com.AgentInsight.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesServiceImpl implements SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);

    private final SalesRepository salesRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;

    @Autowired
    public SalesServiceImpl(SalesRepository salesRepository,
                            UserRepository userRepository,
                            PolicyRepository policyRepository) {
        this.salesRepository = salesRepository;
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        logger.info("SalesServiceImpl initialized");
    }

    @Override
    public List<SaleResponseDTO> getAllSaleWithDetails() {
        logger.info("Fetching all sales with details...");
        List<SaleResponseDTO> sales = salesRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        logger.debug("Retrieved {} sales records", sales.size());
        return sales;
    }

    @Override
    public Page<SaleResponseDTO> getAllSaleWithDetails(Pageable pageable) {
        logger.info("Fetching paginated sales, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<SaleResponseDTO> sales = salesRepository.findAll(pageable).map(this::mapToDto);
        logger.debug("Retrieved {} sales on page {}", sales.getNumberOfElements(), pageable.getPageNumber());
        return sales;
    }

    @Override
    public Optional<SaleResponseDTO> getSaleById(String saleId) {
        logger.info("Fetching sale by ID: {}", saleId);
        return Optional.ofNullable(salesRepository.findById(saleId)
                .map(sale -> {
                    logger.info("Successfully retrieved sale {}", saleId);
                    return mapToDto(sale);
                })
                .orElseThrow(() -> {
                    logger.error("Sale not found: {}", saleId);
                    return new RuntimeException("Sale record not found: " + saleId);
                }));
    }

    @Override
    public SaleResponseDTO addSale(Sales sale) {
        logger.info("Adding new sale...");
        Sales saved = salesRepository.save(sale);
        logger.info("Sale {} added successfully", saved.getSaleid());
        return mapToDto(saved);
    }

    @Override
    public Sales createSale(SaleRequestDto request) {
        logger.info("Creating new sale for agentId: {} and policyId: {}", request.getAgentid(), request.getPolicyid());
        Sales sale = new Sales();
        sale.setSaleamount(Double.valueOf(request.getSaleamount()));
        sale.setSaletype(request.getSaletype());
        sale.setStatus(request.getStatus());

        if (request.getSaledate() != null) {
            sale.setSaledate(Date.from(request.getSaledate().atZone(ZoneId.systemDefault()).toInstant()));
        }

        Users agent = userRepository.findById(request.getAgentid())
                .orElseThrow(() -> {
                    logger.error("Creation failed: Agent ID {} not found", request.getAgentid());
                    return new AgentNotFoundException(request.getAgentid());
                });
        sale.setAgent(agent);

        Policy policy = policyRepository.findByPolicyId(request.getPolicyid())
                .orElseThrow(() -> {
                    logger.error("Creation failed: Policy ID {} not found", request.getPolicyid());
                    return new PolicyNotFoundException(request.getPolicyid());
                });
        sale.setPolicy(policy);

        Sales saved = salesRepository.save(sale);
        logger.info("Sale {} created successfully for agent {}", saved.getSaleid(), agent.getAgentid());
        return saved;
    }

    @Override
    public SaleResponseDTO updateSale(String saleid, SaleRequestDto sale) {
        logger.info("Updating sale with ID: {}", saleid);
        Sales existingSale = salesRepository.findById(saleid)
                .orElseThrow(() -> {
                    logger.error("Update failed: Sale ID {} not found", saleid);
                    return new RuntimeException("Sale record not found: " + saleid);
                });

        if (sale.getSaleamount() != null) {
            existingSale.setSaleamount(Double.valueOf(sale.getSaleamount()));
        }
        if (sale.getSaletype() != null) {
            existingSale.setSaletype(sale.getSaletype());
        }
        if (sale.getSaledate() != null) {
            Date date = Date.from(sale.getSaledate().atZone(ZoneId.systemDefault()).toInstant());
            existingSale.setSaledate(date);
        }
        if (sale.getStatus() != null) {
            existingSale.setStatus(sale.getStatus());
        }
        if (sale.getAgentid() != null) {
            Users agent = userRepository.findById(sale.getAgentid())
                    .orElseThrow(() -> {
                        logger.error("Update failed: Agent ID {} not found", sale.getAgentid());
                        return new AgentNotFoundException(sale.getAgentid());
                    });
            existingSale.setAgent(agent);
        }
        if (sale.getPolicyid() != null) {
            Policy policy = policyRepository.findByPolicyId(sale.getPolicyid())
                    .orElseThrow(() -> {
                        logger.error("Update failed: Policy ID {} not found", sale.getPolicyid());
                        return new PolicyNotFoundException(sale.getPolicyid());
                    });
            existingSale.setPolicy(policy);
        }

        Sales updated = salesRepository.save(existingSale);
        logger.info("Sale {} updated successfully", updated.getSaleid());
        return mapToDto(updated);
    }

    @Override
    public SaleResponseDTO updateSaleStatus(String saleId, String status) {
        logger.info("Updating sale {} status to {}", saleId, status);
        Sales sale = salesRepository.findById(saleId)
                .orElseThrow(() -> {
                    logger.error("Status update failed: Sale ID {} not found", saleId);
                    return new RuntimeException("Sale not found: " + saleId);
                });
        sale.setStatus(status);
        Sales updated = salesRepository.save(sale);
        logger.info("Sale {} status updated successfully to {}", saleId, status);
        return mapToDto(updated);
    }

    @Override
    public void deleteSale(String saleid) {
        logger.info("Deleting sale with ID: {}", saleid);
        if (!salesRepository.existsById(saleid)) {
            logger.error("Delete failed: Sale ID {} not found", saleid);
            throw new RuntimeException("Sale not found with ID: " + saleid);
        }
        salesRepository.deleteById(saleid);
        logger.info("Sale {} deleted successfully", saleid);
    }

    @Override
    public Double getTotalSalesAmount() {
        logger.info("Calculating total sales amount...");
        Double total = salesRepository.findAll().stream()
                .filter(s -> s.getSaleamount() != null)
                .mapToDouble(Sales::getSaleamount)
                .sum();
        logger.info("Total sales amount: {}", total);
        return total;
    }

    @Override
    public List<Users> getAgents() {
        logger.info("Fetching all agents...");
        return userRepository.findAll();
    }

    @Override
    public SaleResponseDTO mapToDto(Sales sale) {
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setSaleid(sale.getSaleid());
        dto.setAmount(sale.getSaleamount());
        dto.setSaletype(sale.getSaletype());
        dto.setStatus(sale.getStatus());

        if (sale.getAgent() != null) {
            dto.setAgentid(sale.getAgent().getAgentid());
            dto.setAgentName(sale.getAgent().getName());
        }

        if (sale.getPolicy() != null) {
            dto.setPolicyid(sale.getPolicy().getPolicyId());
            dto.setPolicyName(sale.getPolicy().getName());
        }

        if (sale.getSaledate() != null) {
            dto.setSaleDate(sale.getSaledate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }

        return dto;
    }

    @Override
    public Double getConversionRate() {
        logger.info("Fetching conversion rate...");
        return 12.5;
    }
}