package com.example.mainservice.controller;

import com.example.mainservice.model.AuthRequest;
import com.example.mainservice.model.AuthResponse;
import com.example.mainservice.model.SignupRequest;
import com.example.mainservice.security.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {AuthController.class, AuthControllerTest.TestSecurityConfig.class})
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuthenticationService authService;

    @Test
    void signup_shouldReturnCreated() throws Exception {
        AuthResponse resp = new AuthResponse("token", "testuser", "ROLE_USER");
        when(authService.signup(any())).thenReturn(resp);
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"testuserpassword\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.token", is("token")));
    }

    @Test
    void login_shouldReturnOk() throws Exception {
        AuthResponse resp = new AuthResponse("token", "testuser", "ROLE_USER");
        when(authService.authenticate(any())).thenReturn(resp);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"testuserpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.token", is("token")));
    }

    @Configuration
    static class TestSecurityConfig {
        @Bean
        public org.springframework.security.web.SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }
}
