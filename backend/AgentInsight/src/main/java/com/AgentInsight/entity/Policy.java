package com.AgentInsight.entity;

import com.AgentInsight.CustomGenerator.PolicyId.GeneratePolicyId;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratePolicyId
    @Column(name = "policy_id", unique = true)
    private String policyId;

    @Column(name = "policy_code", unique = true)
    private String policyCode;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sales> sales;

    // Getters and Setters
    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }

    public String getPolicyCode() { return policyCode; }
    public void setPolicyCode(String policyCode) { this.policyCode = policyCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Sales> getSales() { return sales; }
    public void setSales(List<Sales> sales) { this.sales = sales; }
}
