package com.AgentInsight.service.Impl;

import com.AgentInsight.CustomException.PolicyNotFoundException;
import com.AgentInsight.dto.PolicyDto;
import com.AgentInsight.entity.Policy;
import com.AgentInsight.repository.PolicyRepository;
import com.AgentInsight.service.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyServiceImpl implements PolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceImpl.class);

    @Autowired
    private PolicyRepository policyRepository;

    @Override
    public List<PolicyDto> getAllPolicies() {
        logger.info("Fetching all policies from database");
        return policyRepository.findAllValidPolicies().stream()
                .map(this::convertToDto)
                .filter(dto -> dto != null && dto.getPolicyid() != null)
                .collect(Collectors.toList());
    }

    @Override
    public PolicyDto getPolicyByPolicyId(String policyId) {
        logger.info("Fetching policy with ID: {}", policyId);
        if (policyId == null || policyId.trim().isEmpty()) {
            logger.error("Policy ID is null or empty");
            throw new PolicyNotFoundException("Policy ID cannot be null or empty");
        }
        return policyRepository.findByPolicyId(policyId)
                .map(this::convertToDto)
                .orElseThrow(() -> {
                    logger.error("Policy lookup failed: ID {} not found", policyId);
                    return new PolicyNotFoundException(policyId);
                });
    }

    @Override
    @Transactional
    public Policy createPolicy(Policy policy) {
        logger.info("Creating new policy with code: {}", policy.getPolicyCode());
        // Don't set policyId manually - let the generator handle it
        Policy savedPolicy = policyRepository.save(policy);
        logger.info("Policy created successfully with ID: {} and code: {}", 
            savedPolicy.getPolicyId(), savedPolicy.getPolicyCode());
        return savedPolicy;
    }

    @Override
    @Transactional
    public Policy updatePolicy(Policy policy) {
        logger.info("Updating policy with ID: {}", policy.getPolicyId());

        return policyRepository.findByPolicyId(policy.getPolicyId())
                .map(existingPolicy -> {
                    existingPolicy.setName(policy.getName());
                    existingPolicy.setPolicyCode(policy.getPolicyCode());
                    Policy updated = policyRepository.save(existingPolicy);
                    logger.info("Policy ID {} updated successfully", updated.getPolicyId());
                    return updated;
                })
                .orElseThrow(() -> {
                    logger.error("Update failed: Policy ID {} does not exist", policy.getPolicyId());
                    return new PolicyNotFoundException(policy.getPolicyId());
                });
    }

    @Override
    @Transactional
    public void deletePolicy(String policyId) {
        logger.info("Attempting to delete policy with ID: {}", policyId);

        policyRepository.findByPolicyId(policyId)
                .ifPresentOrElse(
                        policy -> {
                            policyRepository.delete(policy);
                            logger.info("Policy ID {} deleted successfully", policyId);
                        },
                        () -> {
                            logger.error("Delete failed: Policy ID {} not found", policyId);
                            throw new PolicyNotFoundException(policyId);
                        }
                );
    }

    private PolicyDto convertToDto(Policy policy) {
        if (policy == null || policy.getPolicyId() == null) {
            logger.warn("Skipping null or invalid policy: {}", policy);
            return null;
        }
        return new PolicyDto(
                policy.getPolicyId(),
                policy.getPolicyCode(),
                policy.getName()
        );
    }
}