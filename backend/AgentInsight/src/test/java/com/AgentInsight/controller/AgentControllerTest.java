package com.AgentInsight.controller;

import com.AgentInsight.dto.UserDto;
import com.AgentInsight.entity.Users;
import com.AgentInsight.enums.UserRole;
import com.AgentInsight.service.UserService;
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

class AgentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AgentController agentController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(agentController).build();
    }

    @Test
    void getAllAgents_ShouldReturnFilteredAgents() throws Exception {
        UserDto agent = new UserDto();
        agent.setRole(UserRole.AGENT);
        UserDto admin = new UserDto();
        admin.setRole(UserRole.ADMIN);

        List<UserDto> allUsers = Arrays.asList(agent, admin);
        when(userService.getAllUsers()).thenReturn(allUsers);

        mockMvc.perform(get("/agent/loadagents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].role").value("AGENT"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void createAgent_Success() throws Exception {
        Users user = new Users();
        user.setEmail("test@agent.com");
        user.setRole(UserRole.AGENT);
        user.setAgentid("AG123");

        when(userService.createUser(any(Users.class))).thenReturn(user);

        mockMvc.perform(post("/agent/addagent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.agentid").value("AG123"));
    }

    @Test
    void createAgent_Failure() throws Exception {
        Users user = new Users();
        when(userService.createUser(any(Users.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/agent/addagent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void updateUser_Success() throws Exception {
        String agentId = "AG123";
        Users userUpdate = new Users();
        UserDto updatedDto = new UserDto();
        updatedDto.setAgentid(agentId);

        doNothing().when(userService).updateUser(eq(agentId), any(Users.class));
        when(userService.getUserById(agentId)).thenReturn(updatedDto);

        mockMvc.perform(patch("/agent/update/" + agentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentid").value(agentId));
    }

    @Test
    void deleteUser_Success() throws Exception {
        String agentId = "AG123";
        doNothing().when(userService).deleteUser(agentId);

        mockMvc.perform(delete("/agent/delete/" + agentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(userService, times(1)).deleteUser(agentId);
    }

    @Test
    void deleteUser_Failure() throws Exception {
        String agentId = "AG123";
        doThrow(new RuntimeException("Not Found")).when(userService).deleteUser(agentId);

        mockMvc.perform(delete("/agent/delete/" + agentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }
}