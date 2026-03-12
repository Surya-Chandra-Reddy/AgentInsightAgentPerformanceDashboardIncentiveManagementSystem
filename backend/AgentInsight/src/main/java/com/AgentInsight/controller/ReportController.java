package com.AgentInsight.controller;

import com.AgentInsight.dto.AgentReportDTO;
import com.AgentInsight.dto.ResponceDTO.IncentiveResponseDTO;
import com.AgentInsight.dto.ResponceDTO.SaleResponseDTO;
import com.AgentInsight.service.IncentiveService;
import com.AgentInsight.service.SalesService;
import com.AgentInsight.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/reports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final SalesService salesService;
    private final UserService userService;
    private final IncentiveService incentiveService;

    public ReportController(SalesService salesService, UserService userService, IncentiveService incentiveService) {
        this.salesService = salesService;
        this.userService = userService;
        this.incentiveService = incentiveService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/agent-performance")
    public ResponseEntity<List<AgentReportDTO>> getAgentReport() {
        logger.info("Fetching agent performance report...");
        List<AgentReportDTO> report = userService.getAgentReport();
        logger.info("Retrieved {} agent reports", report.size());
        return ResponseEntity.ok(report);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allsales")
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        logger.info("Fetching all sales with details...");
        List<SaleResponseDTO> sales = salesService.getAllSaleWithDetails();
        logger.info("Retrieved {} sales records", sales.size());
        return ResponseEntity.ok(sales);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allincentives")
    public ResponseEntity<List<IncentiveResponseDTO>> getAllIncentives() {
        logger.info("Fetching all incentives with details...");
        List<IncentiveResponseDTO> incentives = incentiveService.getAllIncentivesWithDetails();
        logger.info("Retrieved {} incentives", incentives.size());
        return ResponseEntity.ok(incentives);
    }
}
