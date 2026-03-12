package com.AgentInsight.dto;

public class AgentReportDTO {
    private String agentid;
    private String name;
    private Long salesCount;
    private Double totalSales;

    public AgentReportDTO() {
    }

    private Long incentivesCount;
    private Double totalIncentives;


    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Long salesCount) {
        this.salesCount = salesCount;
    }

    public Double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Double totalSales) {
        this.totalSales = totalSales;
    }

    public Long getIncentivesCount() {
        return incentivesCount;
    }

    public void setIncentivesCount(Long incentivesCount) {
        this.incentivesCount = incentivesCount;
    }

    public Double getTotalIncentives() {
        return totalIncentives;
    }

    public void setTotalIncentives(Double totalIncentives) {
        this.totalIncentives = totalIncentives;
    }

    public AgentReportDTO(String agentid, String name, Long salesCount, Double totalSales, Long incentivesCount, Double totalIncentives) {
        this.agentid = agentid;
        this.name = name;
        this.salesCount = salesCount;
        this.totalSales = totalSales;
        this.incentivesCount = incentivesCount;
        this.totalIncentives = totalIncentives;
    }

    @Override
    public String toString() {
        return "AgentReportDTO{" +
                "agentid='" + agentid + '\'' +
                ", name='" + name + '\'' +
                ", salesCount=" + salesCount +
                ", totalSales=" + totalSales +
                ", incentivesCount=" + incentivesCount +
                ", totalIncentives=" + totalIncentives +
                '}';
    }



}
