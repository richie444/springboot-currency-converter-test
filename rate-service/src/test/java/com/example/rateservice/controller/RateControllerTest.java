package com.example.rateservice.controller;

import com.example.rateservice.model.RateResponse;
import com.example.rateservice.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RateController.class)
@WithMockUser
class RateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        RateResponse mockResponse = new RateResponse("USD", "EUR", new BigDecimal("0.85"));
        when(exchangeRateService.getExchangeRate(anyString(), anyString())).thenReturn(mockResponse);
    }

    @Test
    void getExchangeRate_ShouldReturnRate() throws Exception {
        mockMvc.perform(get("/rate")
                .param("from", "USD")
                .param("to", "EUR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("EUR"))
                .andExpect(jsonPath("$.rate").value(0.85));
    }

    @Test
    void getExchangeRate_WithMissingParams_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/rate")
                .param("from", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}