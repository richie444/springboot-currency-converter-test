package com.example.mainservice.controller;

import com.example.mainservice.model.ConversionResponse;
import com.example.mainservice.service.ConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(ConversionController.class)
@ContextConfiguration(classes = {ConversionController.class, ConversionControllerTest.TestSecurityConfig.class})
class ConversionControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ConversionService service;

    @Configuration
    static class TestSecurityConfig {
        @Bean
        public org.springframework.security.web.SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

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