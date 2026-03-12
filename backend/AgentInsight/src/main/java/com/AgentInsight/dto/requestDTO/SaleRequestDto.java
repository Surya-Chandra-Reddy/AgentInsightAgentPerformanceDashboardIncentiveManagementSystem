package com.AgentInsight.dto.requestDTO;

import java.time.LocalDateTime;

public class SaleRequestDto {
    private String policyid;
    private String agentid;
    private Integer saleamount;
    private String saletype;
    private LocalDateTime saledate; // Using String for date input
    private String status;

    // Default constructor
    public SaleRequestDto() {}

    // Constructor with parameters
    public SaleRequestDto(String policyid, String agentid, Integer saleamount,
                          String saletype, LocalDateTime saledate, String status) {
        this.policyid = policyid;
        this.agentid = agentid;
        this.saleamount = saleamount;
        this.saletype = saletype;
        this.saledate = saledate;
        this.status = status;
    }

    // Getters and Setters
    public String getPolicyid() {
        return policyid;
    }

    public void setPolicyid(String policyid) {
        this.policyid = policyid;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public Integer getSaleamount() {
        return saleamount;
    }

    public void setSaleamount(Integer saleamount) {
        this.saleamount = saleamount;
    }

    public String getSaletype() {
        return saletype;
    }

    public void setSaletype(String saletype) {
        this.saletype = saletype;
    }

    public LocalDateTime getSaledate() {
        return saledate;
    }

    public void setSaledate(LocalDateTime saledate) {
        this.saledate = saledate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
