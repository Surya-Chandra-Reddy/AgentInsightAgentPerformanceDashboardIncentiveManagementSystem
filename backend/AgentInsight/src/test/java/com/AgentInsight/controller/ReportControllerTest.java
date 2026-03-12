package com.AgentInsight.controller;

import com.AgentInsight.dto.AgentReportDTO;
import com.AgentInsight.dto.ResponceDTO.IncentiveResponseDTO;
import com.AgentInsight.dto.ResponceDTO.SaleResponseDTO;
import com.AgentInsight.service.IncentiveService;
import com.AgentInsight.service.SalesService;
import com.AgentInsight.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SalesService salesService;

    @Mock
    private UserService userService;

    @Mock
    private IncentiveService incentiveService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void getAgentReport_ShouldReturnList() throws Exception {
        AgentReportDTO reportDTO = new AgentReportDTO();
        // Assuming common fields for AgentReportDTO
        List<AgentReportDTO> reportList = Arrays.asList(reportDTO);

        when(userService.getAgentReport()).thenReturn(reportList);

        mockMvc.perform(get("/reports/agent-performance")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(userService, times(1)).getAgentReport();
    }

    @Test
    void getAllSales_ShouldReturnList() throws Exception {
        SaleResponseDTO saleDTO = new SaleResponseDTO();
        List<SaleResponseDTO> salesList = Arrays.asList(saleDTO);

        when(salesService.getAllSaleWithDetails()).thenReturn(salesList);

        mockMvc.perform(get("/reports/allsales")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(salesService, times(1)).getAllSaleWithDetails();
    }

    @Test
    void getAllIncentives_ShouldReturnList() throws Exception {
        IncentiveResponseDTO incentiveDTO = new IncentiveResponseDTO();
        List<IncentiveResponseDTO> incentiveList = Arrays.asList(incentiveDTO);

        when(incentiveService.getAllIncentivesWithDetails()).thenReturn(incentiveList);

        mockMvc.perform(get("/reports/allincentives")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(incentiveService, times(1)).getAllIncentivesWithDetails();
    }
}