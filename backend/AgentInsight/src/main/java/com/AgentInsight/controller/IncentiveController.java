package com.AgentInsight.controller;

import com.AgentInsight.dto.ResponceDTO.IncentiveResponseDTO;
import com.AgentInsight.dto.requestDTO.IncentiveRequestDTO;
import com.AgentInsight.service.IncentiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/incentives")
public class IncentiveController {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveController.class);
    private final IncentiveService incentiveService;

    @Autowired
    public IncentiveController(IncentiveService incentiveService) {
        this.incentiveService = incentiveService;
    }

    @GetMapping
    public ResponseEntity<List<IncentiveResponseDTO>> getAllIncentives() {
        logger.info("Fetching all incentives with details...");
        List<IncentiveResponseDTO> incentives = incentiveService.getAllIncentivesWithDetails();
        logger.info("Retrieved {} incentives", incentives.size());
        return ResponseEntity.ok(incentives);
    }

    @GetMapping("/basic")
    public ResponseEntity<List<IncentiveResponseDTO>> getIncentives() {
        logger.info("Fetching basic incentives...");
        List<IncentiveResponseDTO> incentives = incentiveService.getIncentives();
        logger.info("Retrieved {} basic incentives", incentives.size());
        return ResponseEntity.ok(incentives);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<IncentiveResponseDTO>> getAllIncentivesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching paginated incentives, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<IncentiveResponseDTO> incentives = incentiveService.getAllIncentivesWithDetails(pageable);
        logger.info("Retrieved {} incentives on page {}", incentives.getNumberOfElements(), page);
        return ResponseEntity.ok(incentives);
    }

    @GetMapping("/basic/paginated")
    public ResponseEntity<Page<IncentiveResponseDTO>> getIncentivesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching paginated basic incentives, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<IncentiveResponseDTO> incentives = incentiveService.getIncentives(pageable);
        logger.info("Retrieved {} basic incentives on page {}", incentives.getNumberOfElements(), page);
        return ResponseEntity.ok(incentives);
    }

    @GetMapping("/agent/{agentid}")
    public ResponseEntity<List<IncentiveResponseDTO>> getIncentivesByAgentId(@PathVariable String agentid) {
        logger.info("Fetching incentives for agentId: {}", agentid);
        List<IncentiveResponseDTO> incentives = incentiveService.getIncentivesByAgentId(agentid);
        logger.info("Retrieved {} incentives for agentId: {}", incentives.size(), agentid);
        return ResponseEntity.ok(incentives);
    }

    @GetMapping("/{incentiveid}")
    public ResponseEntity<IncentiveResponseDTO> getIncentiveByIncentiveId(@PathVariable String incentiveid) {
        logger.info("Fetching incentive by incentiveId: {}", incentiveid);
        IncentiveResponseDTO incentive = incentiveService.getIncentiveByIncentiveId(incentiveid);
        if (incentive != null) {
            logger.info("Successfully retrieved incentive {}", incentiveid);
            return ResponseEntity.ok(incentive);
        } else {
            logger.warn("No incentive found for incentiveId: {}", incentiveid);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IncentiveResponseDTO> createIncentive(@RequestBody IncentiveRequestDTO incentiveRequest) {
        logger.info("Creating new incentive for agentId: {}", incentiveRequest.getAgentid());
        IncentiveResponseDTO created = incentiveService.createIncentive(incentiveRequest);
        logger.info("Incentive created successfully with id: {}", created.getIncentiveid());
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{incentiveid}/status")
    public ResponseEntity<IncentiveResponseDTO> updateIncentiveStatus(
            @PathVariable String incentiveid,
            @RequestParam String status) {
        logger.info("Updating incentive {} status to {}", incentiveid, status);
        try {
            IncentiveResponseDTO updated = incentiveService.updateIncentiveStatus(incentiveid, status);
            logger.info("Incentive {} status updated successfully to {}", incentiveid, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.error("Failed to update incentive {} status: {}", incentiveid, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/analytics/total-amount")
    public ResponseEntity<Double> getTotalIncentivesAmount() {
        logger.info("Fetching total incentives amount...");
        Double total = incentiveService.getTotalIncentivesAmount();
        logger.info("Total incentives amount: {}", total);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/analytics/total-bonus")
    public ResponseEntity<Double> getTotalBonusAmount() {
        logger.info("Fetching total bonus amount...");
        Double total = incentiveService.getTotalBonusAmount();
        logger.info("Total bonus amount: {}", total);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/analytics/pending-count")
    public ResponseEntity<Integer> getPendingCount() {
        logger.info("Fetching pending incentives count...");
        Integer count = incentiveService.getPendingCount();
        logger.info("Pending incentives count: {}", count);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/reports/allincentives")
    public ResponseEntity<List<IncentiveResponseDTO>> getAllIncentivesWithDetailsForReports() {
        logger.info("Fetching all incentives for reports...");
        List<IncentiveResponseDTO> incentives = incentiveService.getAllIncentivesWithDetailsForReports();
        logger.info("Retrieved {} incentives for reports", incentives.size());
        return ResponseEntity.ok(incentives);
    }

    @GetMapping("/performance/allincentives")
    public ResponseEntity<List<IncentiveResponseDTO>> getAllIncentivesWithDetailsForPerformance() {
        logger.info("Fetching all incentives for performance...");
        List<IncentiveResponseDTO> incentives = incentiveService.getAllIncentivesWithDetailsForPerformance();
        logger.info("Retrieved {} incentives for performance", incentives.size());
        return ResponseEntity.ok(incentives);
    }
}
