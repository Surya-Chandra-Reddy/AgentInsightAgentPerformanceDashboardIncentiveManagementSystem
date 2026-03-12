package com.AgentInsight.service;

import com.AgentInsight.dto.ResponceDTO.SaleResponseDTO;
import com.AgentInsight.dto.requestDTO.SaleRequestDto;
import com.AgentInsight.entity.Sales;
import com.AgentInsight.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SalesService {

    List<SaleResponseDTO> getAllSaleWithDetails();
    Page<SaleResponseDTO> getAllSaleWithDetails(Pageable pageable);

    Optional<SaleResponseDTO> getSaleById(String saleId);
    SaleResponseDTO addSale(Sales sale);
    SaleResponseDTO updateSale(String saleid,SaleRequestDto sale);
    SaleResponseDTO updateSaleStatus(String saleId, String status);
    void deleteSale(String saleid);

    Double getTotalSalesAmount();
    List<Users> getAgents();

    SaleResponseDTO mapToDto(Sales sale);
    Sales createSale(SaleRequestDto request);
    Double getConversionRate();
}
