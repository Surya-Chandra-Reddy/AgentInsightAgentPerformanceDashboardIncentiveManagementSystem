package com.AgentInsight.controller;

import com.AgentInsight.dto.ResponceDTO.SaleResponseDTO;
import com.AgentInsight.dto.requestDTO.SaleRequestDto;
import com.AgentInsight.entity.Sales;
import com.AgentInsight.entity.Users;
import com.AgentInsight.service.SalesService;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SalesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SalesService salesService;

    @InjectMocks
    private SalesController salesController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(salesController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void getAllSales_ShouldReturnList() throws Exception {
        SaleResponseDTO dto = new SaleResponseDTO();
        List<SaleResponseDTO> list = Collections.singletonList(dto);

        when(salesService.getAllSaleWithDetails()).thenReturn(list);

        mockMvc.perform(get("/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getSaleById_Found() throws Exception {
        String id = "S123";
        SaleResponseDTO dto = new SaleResponseDTO();
        when(salesService.getSaleById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/sales/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void addSale_ShouldReturnCreatedSale() throws Exception {
        SaleRequestDto request = new SaleRequestDto();
        request.setAgentid("A001");

        Sales salesEntity = new Sales();
        salesEntity.setSaleid("S999");

        SaleResponseDTO response = new SaleResponseDTO();

        when(salesService.createSale(any(SaleRequestDto.class))).thenReturn(salesEntity);
        when(salesService.mapToDto(any(Sales.class))).thenReturn(response);

        mockMvc.perform(post("/sales/addsales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateSaleStatus_Success() throws Exception {
        String id = "S123";
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "APPROVED");

        when(salesService.updateSaleStatus(eq(id), eq("APPROVED"))).thenReturn(new SaleResponseDTO());

        mockMvc.perform(patch("/sales/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteSale_Success() throws Exception {
        String id = "S123";
        doNothing().when(salesService).deleteSale(id);

        mockMvc.perform(delete("/sales/deletesale/" + id))
                .andExpect(status().isNoContent());

        verify(salesService, times(1)).deleteSale(id);
    }

    @Test
    void getAllSalesDetailsPaginated_ShouldReturnPage() throws Exception {
        SaleResponseDTO dto = new SaleResponseDTO();
        List<SaleResponseDTO> list = new ArrayList<>();
        list.add(dto);
        Page<SaleResponseDTO> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);

        when(salesService.getAllSaleWithDetails(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/sales/salesdetails/paginated")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTotalSaleAmount_ShouldReturnDouble() throws Exception {
        when(salesService.getTotalSalesAmount()).thenReturn(5000.0);

        mockMvc.perform(get("/sales/gettotalsaleamount"))
                .andExpect(status().isOk())
                .andExpect(content().string("5000.0"));
    }

    @Test
    void getAgents_ShouldReturnList() throws Exception {
        Users user = new Users();
        List<Users> agents = Collections.singletonList(user);

        when(salesService.getAgents()).thenReturn(agents);

        mockMvc.perform(get("/sales/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getConversionRate_ShouldReturnDouble() throws Exception {
        when(salesService.getConversionRate()).thenReturn(0.75);

        mockMvc.perform(get("/sales/getconversionrate"))
                .andExpect(status().isOk())
                .andExpect(content().string("0.75"));
    }
}