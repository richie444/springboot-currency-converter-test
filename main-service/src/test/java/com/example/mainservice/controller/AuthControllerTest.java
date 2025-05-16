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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuthenticationService authService;

    @Test
    void signup_shouldReturnCreated() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setUsername("testuser");
        req.setPassword("testuserpassword");
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
        AuthRequest req = new AuthRequest();
        req.setUsername("testuser");
        req.setPassword("testuserpassword");
        AuthResponse resp = new AuthResponse("token", "testuser", "ROLE_USER");
        when(authService.authenticate(any())).thenReturn(resp);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"testuserpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.token", is("token")));
    }
}
