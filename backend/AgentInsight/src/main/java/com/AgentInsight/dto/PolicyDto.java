package com.AgentInsight.dto;

public class PolicyDto {
    private String policyid;
    private String policyCode;
    private String name;

    public PolicyDto() {}

    public PolicyDto(String policyid, String policyCode, String name) {
        this.policyid = policyid;
        this.policyCode = policyCode;
        this.name = name;
    }

    public String getPolicyid() { return policyid; }
    public void setPolicyid(String policyid) { this.policyid = policyid; }

    public String getPolicyCode() { return policyCode; }
    public void setPolicyCode(String policyCode) { this.policyCode = policyCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
