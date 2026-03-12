package com.AgentInsight.repository;

import com.AgentInsight.dto.AgentReportDTO;
import com.AgentInsight.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {

    Users findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT new com.AgentInsight.dto.AgentReportDTO(" +
            "u.agentid, u.name, " +
            "COUNT(DISTINCT s.saleid), " +
            "COALESCE(SUM(s.saleamount), 0.0), " +
            "COUNT(DISTINCT i.incentiveid), " +
            "COALESCE(SUM(i.amount), 0.0)) " +
            "FROM Users u " +
            "LEFT JOIN u.sales s " +
            "LEFT JOIN u.incentives i " +
            "WHERE u.role = com.AgentInsight.enums.UserRole.AGENT " +
            "GROUP BY u.agentid, u.name")
    List<AgentReportDTO> getAgentPerformanceReport();
}
