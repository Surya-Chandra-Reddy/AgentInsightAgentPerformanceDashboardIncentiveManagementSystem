package com.AgentInsight.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "leaderboard")
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_id", unique = true)
    private String entryId;

    @Column(name = "agent_rank")
    private Integer rank;

    @Column(name = "total_sales")
    private Double totalSales;

    @Column(name = "last_updated")
    private Date lastUpdated;

    // Relationships
    @OneToOne
    @JoinColumn(name="agent_id", referencedColumnName = "agentid")
    private Users agent;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public Double getTotalSales() { return totalSales; }
    public void setTotalSales(Double totalSales) { this.totalSales = totalSales; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public Users getAgent() { return agent; }
    public void setAgent(Users agent) { this.agent = agent; }
}
