package com.AgentInsight.entity;

import com.AgentInsight.CustomGenerator.IncentiveId.GeneratedIncentiveId;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "incentives")
public class Incentive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incentiveid", unique = true)
    @GeneratedIncentiveId
    private String incentiveid;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "calculationdate")
    private Date calculationdate;

    @Column(name = "status")
    private String status;

    // Relationships
    @ManyToOne
    @JoinColumn(name="agentid", referencedColumnName = "agentid")
    private Users agent;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIncentiveid() { return incentiveid; }
    public void setIncentiveid(String incentiveid) { this.incentiveid = incentiveid; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Date getCalculationdate() { return calculationdate; }
    public void setCalculationdate(Date calculationdate) { this.calculationdate = calculationdate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Users getAgent() { return agent; }
    public void setAgent(Users agent) { this.agent = agent; }
}
