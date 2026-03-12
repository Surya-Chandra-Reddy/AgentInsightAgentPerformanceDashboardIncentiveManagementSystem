package com.AgentInsight.dto.requestDTO;

import java.util.Date;

public class IncentiveRequestDTO {
    private String agentid;
    private Double amount;
    private Date calculationdate;
    private String status;

    public IncentiveRequestDTO() {}

    public IncentiveRequestDTO(String agentid, Double amount, Date calculationdate, String status) {
        this.agentid = agentid;
        this.amount = amount;
        this.calculationdate = calculationdate;
        this.status = status;
    }

    public String getAgentid() { return agentid; }
    public void setAgentid(String agentid) { this.agentid = agentid; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Date getCalculationdate() { return calculationdate; }
    public void setCalculationdate(Date calculationdate) { this.calculationdate = calculationdate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
