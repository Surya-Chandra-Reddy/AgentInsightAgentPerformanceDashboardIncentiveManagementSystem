package com.AgentInsight.CustomException;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException(String policyId) {
        super("Policy with ID " + policyId + " does not exist.");
    }
}