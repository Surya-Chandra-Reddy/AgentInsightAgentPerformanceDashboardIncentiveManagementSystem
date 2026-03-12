package com.AgentInsight.CustomException;

public class IncentiveNotFoundException extends RuntimeException {
    public IncentiveNotFoundException(String incentiveId) {
        super("Incentive record not found: " + incentiveId);
    }
}