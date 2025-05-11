package com.example.mainservice.controller;

import com.example.mainservice.model.ConversionRequest;
import com.example.mainservice.model.ConversionResponse;
import com.example.mainservice.service.ConversionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversionController.class)
@WithMockUser
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConversionService conversionService;

    @Test
    void convertCurrency_WithValidRequest_ShouldReturnConversion() throws Exception {
        // Prepare test data
        ConversionRequest request = new ConversionRequest();
        request.setFrom("USD");
        request.setTo("EUR");
        request.setAmount(new BigDecimal("100.00"));

        ConversionResponse response = new ConversionResponse(
                "USD", "EUR", new BigDecimal("100.00"),
                new BigDecimal("85.00"), new BigDecimal("0.85")
        );

        when(conversionService.convertCurrency(any(ConversionRequest.class))).thenReturn(response);

        // Execute the test
        mockMvc.perform(post("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("EUR"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.convertedAmount").value(85.00))
                .andExpect(jsonPath("$.rate").value(0.85));
    }

    @Test
    void convertCurrency_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Prepare invalid test data (missing amount)
        ConversionRequest request = new ConversionRequest();
        request.setFrom("USD");
        request.setTo("EUR");
        // Amount is missing

        // Execute the test
        mockMvc.perform(post("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}