package com.AgentInsight.dto;

public class LeaderboardDto {
    private String entryId;
    private String agentId;
    private Integer rank;
    private Double totalSales;
    private String agentName;

    public LeaderboardDto() {}

    public LeaderboardDto(String entryId, String agentId, Integer rank,
                          Double totalSales, String agentName) {
        this.entryId = entryId;
        this.agentId = agentId;
        this.rank = rank;
        this.totalSales = totalSales;
        this.agentName = agentName;
    }

    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public Double getTotalSales() { return totalSales; }
    public void setTotalSales(Double totalSales) { this.totalSales = totalSales; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
}
