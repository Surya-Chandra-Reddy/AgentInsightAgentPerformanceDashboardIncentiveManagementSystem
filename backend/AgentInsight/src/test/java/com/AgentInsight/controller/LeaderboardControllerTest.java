package com.AgentInsight.controller;

import com.AgentInsight.dto.LeaderboardDto;
import com.AgentInsight.entity.Leaderboard;
import com.AgentInsight.service.LeaderboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LeaderboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private LeaderboardController leaderboardController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(leaderboardController).build();
    }

    @Test
    void getLeaderboard_ShouldReturnList() throws Exception {
        LeaderboardDto dto = new LeaderboardDto();
        dto.setEntryId("E001");
        dto.setAgentId("A123");
        dto.setAgentName("John Doe");
        dto.setRank(1);
        dto.setTotalSales(15000.0);

        List<LeaderboardDto> list = Arrays.asList(dto);

        when(leaderboardService.getLeaderboard()).thenReturn(list);

        mockMvc.perform(get("/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].agentName").value("John Doe"))
                .andExpect(jsonPath("$[0].totalSales").value(15000.0));

        verify(leaderboardService, times(1)).getLeaderboard();
    }

    @Test
    void calculateLeaderboard_ShouldReturnRefreshedList() throws Exception {
        LeaderboardDto dto = new LeaderboardDto();
        dto.setAgentId("A456");
        dto.setTotalSales(5000.0);
        dto.setRank(2);

        List<LeaderboardDto> refreshedData = Arrays.asList(dto);

        when(leaderboardService.calculateLeaderboard()).thenReturn(refreshedData);

        mockMvc.perform(post("/leaderboard/calculateLeaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].agentId").value("A456"))
                .andExpect(jsonPath("$[0].totalSales").value(5000.0))
                .andExpect(jsonPath("$[0].rank").value(2));

        verify(leaderboardService, times(1)).calculateLeaderboard();
    }

    @Test
    void update_ShouldReturnUpdatedDto() throws Exception {
        String entryId = "LB123";
        Leaderboard inputEntity = new Leaderboard();
        // Assuming your Leaderboard entity has a field you want to simulate updating

        LeaderboardDto outputDto = new LeaderboardDto();
        outputDto.setEntryId(entryId);
        outputDto.setAgentName("Updated Agent Name");
        outputDto.setRank(5);

        when(leaderboardService.updateLeaderboardEntry(eq(entryId), any(Leaderboard.class)))
                .thenReturn(outputDto);

        mockMvc.perform(put("/leaderboard/" + entryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEntity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryId").value(entryId))
                .andExpect(jsonPath("$.agentName").value("Updated Agent Name"))
                .andExpect(jsonPath("$.rank").value(5));

        verify(leaderboardService, times(1)).updateLeaderboardEntry(eq(entryId), any(Leaderboard.class));
    }
}