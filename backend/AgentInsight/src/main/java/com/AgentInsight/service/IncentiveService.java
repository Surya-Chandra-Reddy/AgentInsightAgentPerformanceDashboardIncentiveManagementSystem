package com.AgentInsight.service;

import com.AgentInsight.dto.ResponceDTO.IncentiveResponseDTO;
import com.AgentInsight.dto.requestDTO.IncentiveRequestDTO;
import com.AgentInsight.entity.Incentive;
import com.AgentInsight.entity.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IncentiveService {
    List<IncentiveResponseDTO> getIncentives();
    List<IncentiveResponseDTO> getAllIncentivesWithDetails();
    Page<IncentiveResponseDTO> getAllIncentivesWithDetails(Pageable pageable);
    Page<IncentiveResponseDTO> getIncentives(Pageable pageable);
    IncentiveResponseDTO getIncentiveByIncentiveId(String incentiveId);
    List<IncentiveResponseDTO> getIncentivesByAgentId(String agentId);

    Double getTotalIncentivesAmount();
    Double getTotalBonusAmount();
    Integer getPendingCount();

    void calculateAndCreateIncentiveForSale(Sales sale);

    List<IncentiveResponseDTO> getAllIncentivesWithDetailsForReports();
    List<IncentiveResponseDTO> getAllIncentivesWithDetailsForPerformance();

    IncentiveResponseDTO updateIncentiveStatus(String incentiveId, String status);
    IncentiveResponseDTO createIncentive(IncentiveRequestDTO incentiveRequest);

    IncentiveResponseDTO convertToDto(Incentive incentive);
    IncentiveResponseDTO convertToDtoWithDetails(Incentive incentive);
}

