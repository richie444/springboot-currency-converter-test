package com.example.mainservice.controller;

import com.example.mainservice.model.ConversionResponse;
import com.example.mainservice.service.ConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(ConversionController.class)
class ConversionControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ConversionService service;

    @Test
    void convertCurrency_shouldReturnOk() throws Exception {
        ConversionResponse resp = new ConversionResponse("USD", "EUR", new BigDecimal("100"), new BigDecimal("85.00"), new BigDecimal("0.85"));
        when(service.convertCurrency(any())).thenReturn(resp);
        mockMvc.perform(post("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from\":\"USD\",\"to\":\"EUR\",\"amount\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from", is("USD")))
                .andExpect(jsonPath("$.to", is("EUR")))
                .andExpect(jsonPath("$.convertedAmount", is(85.00)));
    }
}