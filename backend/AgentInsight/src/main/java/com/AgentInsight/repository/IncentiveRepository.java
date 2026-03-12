package com.AgentInsight.repository;

import com.AgentInsight.entity.Incentive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncentiveRepository extends JpaRepository<Incentive, Long> {
    Optional<Incentive> findByIncentiveid(String incentiveid);

    List<Incentive> findByAgent_Agentid(String agentid);

    List<Incentive> findByStatus(String status);

    Page<Incentive> findByStatus(String status, Pageable pageable);

    void deleteByIncentiveid(String incentiveid);
}
