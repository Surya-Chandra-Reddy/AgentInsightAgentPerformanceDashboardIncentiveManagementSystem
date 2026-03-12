package com.AgentInsight.dto.ResponceDTO;

import java.time.LocalDateTime;

public class SaleResponseDTO {

    private String saleid;
    private String agentid;
    private String agentName;
    private String policyid;
    private String policyName;
    private Double amount;
    private LocalDateTime saleDate;
    private String status;
    private String saletype;

    public SaleResponseDTO() {}

    public SaleResponseDTO(String saleid, String agentid, String agentName,
                           String policyid, String policyName,
                           Double amount, LocalDateTime saleDate, String status) {
        this.saleid = saleid;
        this.agentid = agentid;
        this.agentName = agentName;
        this.policyid = policyid;
        this.policyName = policyName;
        this.amount = amount;
        this.saleDate = saleDate;
        this.status = status;
    }

    public String getSaleid() { return saleid; }
    public void setSaleid(String saleid) { this.saleid = saleid; }

    public String getAgentid() { return agentid; }
    public void setAgentid(String agentid) { this.agentid = agentid; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public String getPolicyid() { return policyid; }
    public void setPolicyid(String policyid) { this.policyid = policyid; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSaletype() { return saletype; }
    public void setSaletype(String saletype) { this.saletype = saletype; }
}
