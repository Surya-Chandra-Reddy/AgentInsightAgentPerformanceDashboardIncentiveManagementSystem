package com.AgentInsight.controller;

import com.AgentInsight.dto.PolicyDto;
import com.AgentInsight.entity.Policy;
import com.AgentInsight.service.PolicyService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PolicyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyController policyController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Explicitly set the Jackson converter to ensure JSON paths match your DTO fields
        mockMvc = MockMvcBuilders.standaloneSetup(policyController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void getAllPolicies_Success() throws Exception {
        PolicyDto p1 = new PolicyDto("P1", "PC01", "Life Insurance");
        List<PolicyDto> list = Arrays.asList(p1);

        when(policyService.getAllPolicies()).thenReturn(list);

        mockMvc.perform(get("/policies")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                // Changed from policyId to policyid to match your DTO
                .andExpect(jsonPath("$[0].policyid").value("P1"))
                .andExpect(jsonPath("$[0].name").value("Life Insurance"));
    }

    @Test
    void getPolicyByPolicyId_Success() throws Exception {
        String id = "P1";
        PolicyDto dto = new PolicyDto(id, "PC01", "Life Insurance");

        when(policyService.getPolicyByPolicyId(id)).thenReturn(dto);

        mockMvc.perform(get("/policies/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Changed from policyId to policyid to match your DTO
                .andExpect(jsonPath("$.policyid").value(id))
                .andExpect(jsonPath("$.policyCode").value("PC01"));
    }

    @Test
    void createPolicy_Success() throws Exception {
        PolicyDto requestDto = new PolicyDto(null, "PC01", "New Policy");

        Policy savedPolicy = new Policy();
        // Assuming your Entity might still use policyId, but the Controller
        // maps it back to the DTO's policyid field in the response
        savedPolicy.setPolicyId("P-Generated");
        savedPolicy.setPolicyCode("PC01");
        savedPolicy.setName("New Policy");

        when(policyService.createPolicy(any(Policy.class))).thenReturn(savedPolicy);

        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // The controller response uses new PolicyDto(created.getPolicyId(), ...)
                // so the JSON key will be "policyid"
                .andExpect(jsonPath("$.policyid").value("P-Generated"))
                .andExpect(jsonPath("$.name").value("New Policy"));
    }

    @Test
    void updatePolicy_Success() throws Exception {
        String id = "P1";
        PolicyDto updateRequest = new PolicyDto(id, "PC01-UPD", "Updated Name");

        Policy updatedPolicy = new Policy();
        updatedPolicy.setPolicyId(id);
        updatedPolicy.setPolicyCode("PC01-UPD");
        updatedPolicy.setName("Updated Name");

        when(policyService.updatePolicy(any(Policy.class))).thenReturn(updatedPolicy);

        mockMvc.perform(put("/policies/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyid").value(id))
                .andExpect(jsonPath("$.policyCode").value("PC01-UPD"));
    }

    @Test
    void deletePolicy_Success() throws Exception {
        String id = "P1";
        doNothing().when(policyService).deletePolicy(id);

        mockMvc.perform(delete("/policies/" + id))
                .andExpect(status().isNoContent());

        verify(policyService, times(1)).deletePolicy(id);
    }

    @Test
    void getAllPolicies_Error() throws Exception {
        when(policyService.getAllPolicies()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/policies"))
                .andExpect(status().isBadRequest());
    }
}