package com.AgentInsight.controller;

import com.AgentInsight.CustomException.EmailAlreadyExistsException;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void register_Success() throws Exception {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setRole(UserRole.AGENT);

        doNothing().when(userService).addUser(any(Users.class));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.role").value("AGENT"));
    }

    @Test
    void register_EmailExists() throws Exception {
        Users user = new Users();
        user.setEmail("exists@example.com");

        doThrow(new EmailAlreadyExistsException("Email already exists"))
                .when(userService).addUser(any(Users.class));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Error registering"));
    }

    @Test
    void createAgent_Success() throws Exception {
        Users user = new Users();
        user.setEmail("newagent@example.com");
        user.setRole(UserRole.AGENT);

        Users savedUser = new Users();
        savedUser.setAgentid("AG999");
        savedUser.setRole(UserRole.AGENT);

        when(userService.createUser(any(Users.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentid").value("AG999"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void login_Success() throws Exception {
        Users user = new Users();
        user.setEmail("test@example.com");

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", "jwt-token");

        when(userService.verify(any(Users.class))).thenReturn(tokenMap);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        UserDto dto = new UserDto("AG123", "John Doe", "john@example.com", "1234567890", UserRole.AGENT);
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].agentid").value("AG123"));
    }

    @Test
    void getUserById_Success() throws Exception {
        String agentId = "AG123";
        UserDto dto = new UserDto(agentId, "John", "john@mail.com", "123", UserRole.AGENT);

        when(userService.getUserById(agentId)).thenReturn(dto);

        mockMvc.perform(get("/users/" + agentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentid").value(agentId));
    }

    @Test
    void deleteUser_Success() throws Exception {
        String agentId = "AG123";
        doNothing().when(userService).deleteUser(agentId);

        mockMvc.perform(delete("/users/" + agentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void updateUser_Success() throws Exception {
        String agentId = "AG123";
        Users userUpdate = new Users();
        UserDto updatedDto = new UserDto(agentId, "Updated", "u@m.com", "999", UserRole.AGENT);

        doNothing().when(userService).updateUser(eq(agentId), any(Users.class));
        when(userService.getUserById(agentId)).thenReturn(updatedDto);

        mockMvc.perform(patch("/users/" + agentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }
}