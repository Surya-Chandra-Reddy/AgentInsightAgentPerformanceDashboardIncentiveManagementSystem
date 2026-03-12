package com.AgentInsight.repository;

import com.AgentInsight.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query("SELECT p FROM Policy p WHERE p.policyId = :policyId")
    Optional<Policy> findByPolicyId(@Param("policyId") String policyId);

    @Query("SELECT p FROM Policy p WHERE p.policyCode = :policyCode")
    Optional<Policy> findByPolicyCode(@Param("policyCode") String policyCode);

    @Query("DELETE FROM Policy p WHERE p.policyId = :policyId")
    void deleteByPolicyId(@Param("policyId") String policyId);

    @Query("SELECT p FROM Policy p WHERE p.policyId IS NOT NULL ORDER BY p.policyId DESC")
    List<Policy> findAllOrderByPolicyIdDesc();

    @Query("SELECT p FROM Policy p WHERE p.policyId IS NOT NULL")
    List<Policy> findAllValidPolicies();
}
