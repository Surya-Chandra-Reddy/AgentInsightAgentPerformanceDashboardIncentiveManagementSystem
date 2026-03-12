package com.AgentInsight.controller;

import com.AgentInsight.dto.PolicyDto;
import com.AgentInsight.entity.Policy;
import com.AgentInsight.service.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/policies")
public class PolicyController {

    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    private PolicyService policyService;

    @GetMapping
    public ResponseEntity<List<PolicyDto>> getAllPolicies() {
        logger.info("Fetching all policies...");
        try {
            List<PolicyDto> policies = policyService.getAllPolicies();
            logger.info("Retrieved {} policies", policies.size());
            return ResponseEntity.ok(policies);
        } catch (Exception e) {
            logger.error("Error fetching policies", e);
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<PolicyDto> getPolicyByPolicyId(@PathVariable String policyId) {
        logger.info("Fetching policy with ID: {}", policyId);
        try {
            PolicyDto policy = policyService.getPolicyByPolicyId(policyId);
            if (policy != null) {
                logger.info("Successfully retrieved policy {}", policyId);
                return ResponseEntity.ok(policy);
            }
            logger.warn("No policy found with ID: {}", policyId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching policy {}", policyId, e);
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping
    public ResponseEntity<PolicyDto> createPolicy(@RequestBody PolicyDto policyDto) {
        logger.info("Creating new policy with code: {}", policyDto.getPolicyCode());
        try {
            Policy policy = new Policy();
            policy.setPolicyCode(policyDto.getPolicyCode());
            policy.setName(policyDto.getName());
            // DON'T set policyId - let generator handle it

            Policy created = policyService.createPolicy(policy);
            PolicyDto responseDto = new PolicyDto(
                    created.getPolicyId(),
                    created.getPolicyCode(),
                    created.getName()
            );
            logger.info("Policy created successfully with ID: {}", created.getPolicyId());
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            logger.error("Error creating policy", e);
            return ResponseEntity.status(400).build();
        }
    }

    @PutMapping("/{policyId}")
    public ResponseEntity<PolicyDto> updatePolicy(@PathVariable String policyId, @RequestBody PolicyDto policyDto) {
        logger.info("Updating policy with ID: {}", policyId);
        try {
            Policy policy = new Policy();
            policy.setPolicyId(policyId);
            policy.setPolicyCode(policyDto.getPolicyCode());
            policy.setName(policyDto.getName());

            Policy updated = policyService.updatePolicy(policy);
            PolicyDto responseDto = new PolicyDto(
                    updated.getPolicyId(),
                    updated.getPolicyCode(),
                    updated.getName()
            );
            logger.info("Policy {} updated successfully", policyId);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            logger.error("Error updating policy {}", policyId, e);
            return ResponseEntity.status(400).build();
        }
    }

    @DeleteMapping("/{policyId}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String policyId) {
        logger.info("Deleting policy with ID: {}", policyId);
        try {
            policyService.deletePolicy(policyId);
            logger.info("Policy {} deleted successfully", policyId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting policy {}", policyId, e);
            return ResponseEntity.status(400).build();
        }
    }
}
