package com.AgentInsight.controller;

import com.AgentInsight.dto.ResponceDTO.SaleResponseDTO;
import com.AgentInsight.dto.requestDTO.SaleRequestDto;
import com.AgentInsight.entity.Sales;
import com.AgentInsight.entity.Users;
import com.AgentInsight.service.SalesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/sales")
public class SalesController {

    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);
    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
        logger.info("SalesController initialized");
    }

    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        logger.info("Fetching all sales with details...");
        List<SaleResponseDTO> sales = salesService.getAllSaleWithDetails();
        logger.info("Retrieved {} sales records", sales.size());
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable String saleId) {
        logger.info("Fetching sale by ID: {}", saleId);
        return salesService.getSaleById(saleId)
                .map(sale -> {
                    logger.info("Successfully retrieved sale {}", saleId);
                    return ResponseEntity.ok(sale);
                })
                .orElseGet(() -> {
                    logger.warn("No sale found with ID: {}", saleId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/addsales")
    public ResponseEntity<SaleResponseDTO> addSale(@RequestBody SaleRequestDto request) {
        logger.info("Adding new sale for agentId: {}", request.getAgentid());
        Sales createdSale = salesService.createSale(request);
        SaleResponseDTO response = salesService.mapToDto(createdSale);
        logger.info("Sale created successfully with ID: {}", createdSale.getSaleid());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updatesale/{saleid}")
    public ResponseEntity<SaleResponseDTO> updateSaleById(@PathVariable String saleid, @RequestBody SaleRequestDto sale) {
        logger.info("Updating sale with ID: {}", saleid);
        SaleResponseDTO updated = salesService.updateSale(saleid, sale);
        logger.info("Sale {} updated successfully", saleid);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> updateSaleStatus(@PathVariable String id,
                                                            @RequestBody Map<String, String> statusUpdate) {
        try {
            String status = statusUpdate.get("status");
            if (status == null) {
                logger.warn("Status update request missing 'status' field for sale {}", id);
                return ResponseEntity.badRequest().build();
            }
            logger.info("Updating sale {} to status {}", id, status);
            SaleResponseDTO updated = salesService.updateSaleStatus(id, status);
            logger.info("Sale {} status updated successfully to {}", id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating sale {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletesale/{saleid}")
    public ResponseEntity<Void> deleteSale(@PathVariable String saleid) {
        logger.info("Deleting sale with ID: {}", saleid);
        salesService.deleteSale(saleid);
        logger.info("Sale {} deleted successfully", saleid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/salesdetails")
    public ResponseEntity<List<SaleResponseDTO>> getAllSalesDetails() {
        logger.info("Fetching all sales details...");
        List<SaleResponseDTO> sales = salesService.getAllSaleWithDetails();
        logger.info("Retrieved {} sales records with details", sales.size());
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/salesdetails/paginated")
    public ResponseEntity<Page<SaleResponseDTO>> getAllSalesDetailsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching paginated sales details, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<SaleResponseDTO> sales = salesService.getAllSaleWithDetails(pageable);
        logger.info("Retrieved {} sales records on page {}", sales.getNumberOfElements(), page);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/gettotalsaleamount")
    public Double getTotalSaleAmount() {
        logger.info("Fetching total sales amount...");
        Double total = salesService.getTotalSalesAmount();
        logger.info("Total sales amount: {}", total);
        return total;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> getAgents() {
        logger.info("Fetching all agents...");
        List<Users> agents = salesService.getAgents();
        logger.info("Retrieved {} agents", agents.size());
        return agents;
    }

    @GetMapping("/getconversionrate")
    public Double getConversionRate() {
        logger.info("Fetching conversion rate...");
        Double rate = salesService.getConversionRate();
        logger.info("Conversion rate: {}", rate);
        return rate;
    }
}
