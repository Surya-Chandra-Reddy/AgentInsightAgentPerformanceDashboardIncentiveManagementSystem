package com.AgentInsight.controller;

import com.AgentInsight.dto.AgentPerformanceDTO;
import com.AgentInsight.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PerformanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private PerformanceController performanceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(performanceController).build();
    }

    @Test
    void getAgentPerformance_Success() throws Exception {
        String agentId = "AG001";
        AgentPerformanceDTO performanceDTO = new AgentPerformanceDTO();

        // Setting fields exactly as defined in your DTO
        performanceDTO.setAgentid(agentId);
        performanceDTO.setName("John Doe");
        performanceDTO.setEmail("john@example.com");
        performanceDTO.setPhone("1234567890");
        performanceDTO.setTotalSales(50000.0);
        performanceDTO.setTotalIncentives(1500.0);
        performanceDTO.setSales(new ArrayList<>());
        performanceDTO.setIncentives(new ArrayList<>());

        when(userService.getAgentPerformanceById(agentId)).thenReturn(performanceDTO);

        mockMvc.perform(get("/performance/" + agentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentid").value(agentId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.totalSales").value(50000.0))
                .andExpect(jsonPath("$.totalIncentives").value(1500.0));

        verify(userService, times(1)).getAgentPerformanceById(agentId);
    }

    @Test
    void getAgentPerformance_NotFound() throws Exception {
        String agentId = "INVALID_ID";

        when(userService.getAgentPerformanceById(agentId)).thenReturn(null);

        mockMvc.perform(get("/performance/" + agentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(userService, times(1)).getAgentPerformanceById(agentId);
    }
}