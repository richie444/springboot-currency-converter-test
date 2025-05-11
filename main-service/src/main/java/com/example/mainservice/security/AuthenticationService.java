package com.example.mainservice.security;

import com.example.mainservice.model.AuthRequest;
import com.example.mainservice.model.AuthResponse;
import com.example.mainservice.model.SignupRequest;
import com.example.mainservice.model.User;
import com.example.mainservice.repository.UserRepository;
import com.example.mainservice.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtUtil jwtUtil,
                                 UserRepository userRepository,
                                 UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Register a new user and generate a JWT token
     * @param signupRequest The signup request
     * @return AuthResponse containing token and user info
     * @throws IllegalArgumentException if username already exists
     */
    public AuthResponse signup(SignupRequest signupRequest) {
        // Create user with ROLE_USER role
        User user = userService.createUser(
                signupRequest.getUsername(),
                signupRequest.getPassword(),
                "ROLE_USER"
        );

        // Create JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
        
        String token = jwtUtil.generateToken(userDetails);
        
        // Return auth response
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    /**
     * Authenticate a user and generate a JWT token
     * @param authRequest The authentication request
     * @return AuthResponse containing token and user info
     * @throws AuthenticationException if authentication fails
     */
    public AuthResponse authenticate(AuthRequest authRequest) {
        try {
            // Authenticate user with Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
                )
            );

            // Get user details after authentication
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Find the user entity with additional details
            Optional<User> userOpt = userRepository.findByUsername(userDetails.getUsername());
            
            if (userOpt.isEmpty()) {
                throw new AuthenticationException("User details not found", "USER_NOT_FOUND");
            }
            
            User user = userOpt.get();
            
            // Check if user account is enabled
            if (!user.isEnabled()) {
                throw new AuthenticationException.AccountDisabledException();
            }
            
            // Create JWT token
            String token = jwtUtil.generateToken(userDetails);
            
            // Create response with token and user details
            return new AuthResponse(token, user.getUsername(), user.getRole());
            
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new AuthenticationException.InvalidCredentialsException();
        }
    }
    
    /**
     * Validate a JWT token
     * @param token The JWT token to validate
     * @return User associated with the token if valid
     * @throws AuthenticationException.TokenValidationException if token validation fails
     */
    public User validateToken(String token) {
        if (token == null) {
            throw new AuthenticationException.TokenValidationException("Token is required");
        }
        
        try {
            String username = jwtUtil.extractUsername(token);
            Optional<User> userOpt = userRepository.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                throw new AuthenticationException.TokenValidationException("User not found");
            }
            
            User user = userOpt.get();
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .authorities(user.getRole())
                .build();
            
            boolean isValid = jwtUtil.validateToken(token, userDetails);
            
            if (!isValid) {
                throw new AuthenticationException.TokenValidationException("Invalid token");
            }
            
            return user;
            
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw e;
            }
            throw new AuthenticationException.TokenValidationException("Token validation error: " + e.getMessage());
        }
    }
}