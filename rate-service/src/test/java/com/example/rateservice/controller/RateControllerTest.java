package com.example.rateservice.controller;

import com.example.rateservice.model.RateResponse;
import com.example.rateservice.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(RateController.class)
@ContextConfiguration(classes = {RateController.class, RateControllerTest.TestSecurityConfig.class})
class RateControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ExchangeRateService service;

    @Configuration
    static class TestSecurityConfig {
        @Bean
        public org.springframework.security.web.SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void getExchangeRate_shouldReturnOk() throws Exception {
        RateResponse resp = new RateResponse("USD", "EUR", new BigDecimal("0.85"));
        when(service.getExchangeRate(anyString(), anyString())).thenReturn(resp);
        mockMvc.perform(get("/rate")
                .param("from", "USD")
                .param("to", "EUR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from", is("USD")))
                .andExpect(jsonPath("$.to", is("EUR")))
                .andExpect(jsonPath("$.rate", is(0.85)));
    }
}