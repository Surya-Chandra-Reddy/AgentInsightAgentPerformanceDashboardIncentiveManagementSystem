package com.AgentInsight.controller;

import com.AgentInsight.dto.AgentPerformanceDTO;
import com.AgentInsight.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("/performance")
public class PerformanceController {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceController.class);

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{agentId}")
    public ResponseEntity<AgentPerformanceDTO> getAgentPerformance(@PathVariable String agentId) {
        logger.info("Fetching performance report for agentId: {}", agentId);
        AgentPerformanceDTO performance = userService.getAgentPerformanceById(agentId);
        if (performance != null) {
            logger.info("Successfully retrieved performance for agentId: {}", agentId);
        } else {
            logger.warn("No performance data found for agentId: {}", agentId);
        }
        return ResponseEntity.ok(performance);
    }
}
