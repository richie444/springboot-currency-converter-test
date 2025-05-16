package com.example.mainservice.controller;

import com.example.mainservice.security.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuthenticationService authService;

    @Test
    void signup_shouldReturnCreated() throws Exception {
        when(authService.signup(any())).thenReturn(null);
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"testuserpassword\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void login_shouldReturnOk() throws Exception {
        when(authService.authenticate(any())).thenReturn(null);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"testuserpassword\"}"))
                .andExpect(status().isOk());
    }
}
