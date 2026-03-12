package com.AgentInsight.dto.ResponceDTO;

import java.util.Date;

public class IncentiveResponseDTO {
    private String incentiveid;
    private String agentid;
    private Double amount;
    private Date calculationdate;
    private String status;
    private String agentName;
    private Double bonus;

    public IncentiveResponseDTO() {}

    public IncentiveResponseDTO(String incentiveid, String agentid, Double amount,
                                Date calculationdate, String status,
                                String agentName, Double bonus) {
        this.incentiveid = incentiveid;
        this.agentid = agentid;
        this.amount = amount;
        this.calculationdate = calculationdate;
        this.status = status;
        this.agentName = agentName;
        this.bonus = bonus;
    }

    public String getIncentiveid() { return incentiveid; }
    public void setIncentiveid(String incentiveid) { this.incentiveid = incentiveid; }

    public String getAgentid() { return agentid; }
    public void setAgentid(String agentid) { this.agentid = agentid; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Date getCalculationdate() { return calculationdate; }
    public void setCalculationdate(Date calculationdate) { this.calculationdate = calculationdate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public Double getBonus() { return bonus; }
    public void setBonus(Double bonus) { this.bonus = bonus; }
}
