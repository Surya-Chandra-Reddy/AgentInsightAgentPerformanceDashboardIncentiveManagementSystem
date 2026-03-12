package com.AgentInsight.service;

import com.AgentInsight.dto.PolicyDto;
import com.AgentInsight.entity.Policy;

import java.util.List;

public interface PolicyService {
    List<PolicyDto> getAllPolicies();
    PolicyDto getPolicyByPolicyId(String policyId);
    Policy createPolicy(Policy policy);
    Policy updatePolicy(Policy policy);
    void deletePolicy(String policyId);
}
