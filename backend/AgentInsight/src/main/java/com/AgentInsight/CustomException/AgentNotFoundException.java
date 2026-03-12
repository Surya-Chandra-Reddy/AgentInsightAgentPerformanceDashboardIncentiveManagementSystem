package com.AgentInsight.CustomException;

public class AgentNotFoundException extends RuntimeException {
    public String getAgentid() {
        return agentid;
    }

    private final String agentid;

    public AgentNotFoundException(String agentid) {
        super("Agent not found with ID: " + agentid);
        this.agentid = agentid;
    }
}