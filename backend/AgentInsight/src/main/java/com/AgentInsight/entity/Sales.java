package com.AgentInsight.entity;

import com.AgentInsight.CustomGenerator.SaleId.GeneratedSaleId;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name="sales")
public class Sales {

    @Id
    @GeneratedSaleId
    @Column(name="saleid")
    private String saleid;

    @Column(name="saleamount")
    private Double saleamount;

    @Column(name="saletype")
    private String saletype;

    @Column(name="saledate")
    private Date saledate;

    @Column(name="status")
    private String status;

    // Relationships
    @ManyToOne
    @JoinColumn(name="agentid", referencedColumnName = "agentid")
    private Users agent;

    @ManyToOne
    @JoinColumn(name="policyid", referencedColumnName = "policy_id")
    private Policy policy;

    // Getters and Setters
    public String getSaleid() { return saleid; }
    public void setSaleid(String saleid) { this.saleid = saleid; }

    public Double getSaleamount() { return saleamount; }
    public void setSaleamount(Double saleamount) { this.saleamount = saleamount; }

    public String getSaletype() { return saletype; }
    public void setSaletype(String saletype) { this.saletype = saletype; }

    public Date getSaledate() { return saledate; }
    public void setSaledate(Date saledate) { this.saledate = saledate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Users getAgent() { return agent; }
    public void setAgent(Users agent) { this.agent = agent; }

    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }
}
