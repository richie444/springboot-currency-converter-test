package com.example.mainservice.security;

import com.example.mainservice.model.AuthRequest;
import com.example.mainservice.model.AuthResponse;
import com.example.mainservice.model.SignupRequest;
import com.example.mainservice.model.User;
import com.example.mainservice.repository.UserRepository;
import com.example.mainservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User("testuser", "encodedPassword", "ROLE_USER");
        testUser.setId(1L);
        testUser.setEnabled(true);
        testUser.setCreatedAt(LocalDateTime.now());
        
        // Setup Spring Security UserDetails
        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
                
        // Setup Authentication object
        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, "encodedPassword", userDetails.getAuthorities());
    }

    @Test
    void signup_ShouldCreateUserAndGenerateToken() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setPassword("password123");
        
        when(userService.createUser(anyString(), anyString(), eq("ROLE_USER")))
                .thenReturn(testUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("test-jwt-token");

        // Act
        AuthResponse response = authenticationService.signup(signupRequest);

        // Assert
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
        
        // Verify interactions
        verify(userService).createUser(eq("newuser"), eq("password123"), eq("ROLE_USER"));
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    void authenticate_ShouldGenerateToken_WhenCredentialsAreValid() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("test-jwt-token");

        // Act
        AuthResponse response = authenticationService.authenticate(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
        
        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testuser");
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> 
            authenticationService.authenticate(authRequest));
            
        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testuser");
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }
    
    @Test
    void authenticate_ShouldThrowException_WhenUserIsDisabled() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");
        
        // Create disabled user
        User disabledUser = new User("testuser", "encodedPassword", "ROLE_USER");
        disabledUser.setEnabled(false);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(disabledUser));

        // Act & Assert
        assertThrows(AuthenticationException.AccountDisabledException.class, () -> 
            authenticationService.authenticate(authRequest));
            
        // Verify interactions
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }
    
    @Test
    void authenticate_ShouldThrowException_WhenAuthenticationFails() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("wrongpassword");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(AuthenticationException.InvalidCredentialsException.class, () -> 
            authenticationService.authenticate(authRequest));
            
        // Verify interactions
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }
    
    @Test
    void validateToken_ShouldReturnUser_WhenTokenIsValid() {
        // Arrange
        String token = "valid-token";
        
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken(eq(token), any(UserDetails.class))).thenReturn(true);

        // Act
        User result = authenticationService.validateToken(token);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        
        // Verify interactions
        verify(jwtUtil).extractUsername(token);
        verify(userRepository).findByUsername("testuser");
        verify(jwtUtil).validateToken(eq(token), any(UserDetails.class));
    }
    
    @Test
    void validateToken_ShouldThrowException_WhenTokenIsNull() {
        // Act & Assert
        assertThrows(AuthenticationException.TokenValidationException.class, () -> 
            authenticationService.validateToken(null));
    }
    
    @Test
    void validateToken_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String token = "valid-token";
        
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthenticationException.TokenValidationException.class, () -> 
            authenticationService.validateToken(token));
            
        // Verify interactions
        verify(jwtUtil).extractUsername(token);
        verify(userRepository).findByUsername("testuser");
        verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));
    }
    
    @Test
    void validateToken_ShouldThrowException_WhenTokenIsInvalid() {
        // Arrange
        String token = "invalid-token";
        
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken(eq(token), any(UserDetails.class))).thenReturn(false);

        // Act & Assert
        assertThrows(AuthenticationException.TokenValidationException.class, () -> 
            authenticationService.validateToken(token));
            
        // Verify interactions
        verify(jwtUtil).extractUsername(token);
        verify(userRepository).findByUsername("testuser");
        verify(jwtUtil).validateToken(eq(token), any(UserDetails.class));
    }
}
