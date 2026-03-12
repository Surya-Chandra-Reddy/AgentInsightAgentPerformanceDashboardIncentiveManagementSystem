package com.AgentInsight.controller;

import com.AgentInsight.dto.ResponceDTO.IncentiveResponseDTO;
import com.AgentInsight.dto.requestDTO.IncentiveRequestDTO;
import com.AgentInsight.service.IncentiveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class IncentiveControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IncentiveService incentiveService;

    @InjectMocks
    private IncentiveController incentiveController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(incentiveController).build();
    }

    @Test
    void getAllIncentives_ShouldReturnList() throws Exception {
        List<IncentiveResponseDTO> list = Collections.singletonList(new IncentiveResponseDTO());
        when(incentiveService.getAllIncentivesWithDetails()).thenReturn(list);

        mockMvc.perform(get("/incentives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getAllIncentivesPaginated_ShouldReturnPage() throws Exception {
        IncentiveResponseDTO dto = new IncentiveResponseDTO();
        // Use an ArrayList to ensure the list is mutable
        List<IncentiveResponseDTO> list = new java.util.ArrayList<>();
        list.add(dto);

        // Create Pageable matching the default params (0, 10)
        Pageable pageable = PageRequest.of(0, 10);
        Page<IncentiveResponseDTO> page = new PageImpl<>(list, pageable, 1);

        when(incentiveService.getAllIncentivesWithDetails(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/incentives/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)) // Explicitly ask for JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    @Test
    void getIncentivesByAgentId_ShouldReturnList() throws Exception {
        String agentId = "A123";
        when(incentiveService.getIncentivesByAgentId(agentId)).thenReturn(Collections.singletonList(new IncentiveResponseDTO()));

        mockMvc.perform(get("/incentives/agent/" + agentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getIncentiveByIncentiveId_Found() throws Exception {
        String id = "INC001";
        IncentiveResponseDTO dto = new IncentiveResponseDTO();
        dto.setIncentiveid(id);
        when(incentiveService.getIncentiveByIncentiveId(id)).thenReturn(dto);

        mockMvc.perform(get("/incentives/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incentiveid").value(id));
    }

    @Test
    void getIncentiveByIncentiveId_NotFound() throws Exception {
        when(incentiveService.getIncentiveByIncentiveId("invalid")).thenReturn(null);

        mockMvc.perform(get("/incentives/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createIncentive_ShouldReturnCreated() throws Exception {
        IncentiveRequestDTO request = new IncentiveRequestDTO();
        request.setAgentid("A123");
        IncentiveResponseDTO response = new IncentiveResponseDTO();
        response.setIncentiveid("INC001");

        when(incentiveService.createIncentive(any(IncentiveRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/incentives/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incentiveid").value("INC001"));
    }

    @Test
    void updateIncentiveStatus_Success() throws Exception {
        String id = "INC001";
        String status = "APPROVED";
        IncentiveResponseDTO response = new IncentiveResponseDTO();
        response.setStatus(status);

        when(incentiveService.updateIncentiveStatus(eq(id), eq(status))).thenReturn(response);

        mockMvc.perform(patch("/incentives/" + id + "/status")
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(status));
    }

    @Test
    void updateIncentiveStatus_Failure() throws Exception {
        when(incentiveService.updateIncentiveStatus(anyString(), anyString())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(patch("/incentives/INC001/status")
                        .param("status", "PAID"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTotalIncentivesAmount_ShouldReturnDouble() throws Exception {
        when(incentiveService.getTotalIncentivesAmount()).thenReturn(5000.0);

        mockMvc.perform(get("/incentives/analytics/total-amount"))
                .andExpect(status().isOk())
                .andExpect(content().string("5000.0"));
    }

    @Test
    void getPendingCount_ShouldReturnInteger() throws Exception {
        when(incentiveService.getPendingCount()).thenReturn(5);

        mockMvc.perform(get("/incentives/analytics/pending-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}