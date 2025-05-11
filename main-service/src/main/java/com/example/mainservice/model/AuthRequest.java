package com.example.mainservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Authentication request for login")
public class AuthRequest {
    
    @Schema(description = "Username for authentication", example = "user", required = true)
    @NotBlank(message = "Username is required")
    private String username;
    
    @Schema(description = "Password for authentication", example = "password", required = true, format = "password")
    @NotBlank(message = "Password is required")
    private String password;

    // Default constructor required for Jackson
    public AuthRequest() {
    }

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}