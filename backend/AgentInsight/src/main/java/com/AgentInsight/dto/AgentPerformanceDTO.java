package com.AgentInsight.dto;

import com.AgentInsight.dto.ResponceDTO.IncentiveResponseDTO;
import com.AgentInsight.dto.ResponceDTO.SaleResponseDTO;
import java.util.List;

public class AgentPerformanceDTO {
    private String agentid;
    private String name;
    private String email;
    private String phone;
    private List<SaleResponseDTO> sales;
    private List<IncentiveResponseDTO> incentives;
    private double totalSales;
    private double totalIncentives;

    public AgentPerformanceDTO() {}

    public AgentPerformanceDTO(String agentid, String name, String email, String phone,
                               List<SaleResponseDTO> sales,
                               List<IncentiveResponseDTO> incentives,
                               double totalSales,
                               double totalIncentives) {
        this.agentid = agentid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.sales = sales;
        this.incentives = incentives;
        this.totalSales = totalSales;
        this.totalIncentives = totalIncentives;
    }

    // Getters and setters
    public String getAgentid() { return agentid; }
    public void setAgentid(String agentid) { this.agentid = agentid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<SaleResponseDTO> getSales() { return sales; }
    public void setSales(List<SaleResponseDTO> sales) { this.sales = sales; }

    public List<IncentiveResponseDTO> getIncentives() { return incentives; }
    public void setIncentives(List<IncentiveResponseDTO> incentives) { this.incentives = incentives; }

    public double getTotalSales() { return totalSales; }
    public void setTotalSales(double totalSales) { this.totalSales = totalSales; }

    public double getTotalIncentives() { return totalIncentives; }
    public void setTotalIncentives(double totalIncentives) { this.totalIncentives = totalIncentives; }
}
