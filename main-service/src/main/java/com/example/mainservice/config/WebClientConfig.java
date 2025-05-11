package com.example.mainservice.config;

import com.example.mainservice.model.User;
import com.example.mainservice.repository.UserRepository;
import com.example.mainservice.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Optional;

@Configuration
@EnableScheduling
public class WebClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${rate-service.base-url:http://rate-service:8081}")
    private String rateServiceBaseUrl;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${rate-service.username:user}")
    private String serviceUsername;
    
    private WebClient webClient;
    private String currentToken;
    
    @PostConstruct
    public void init() {
        refreshToken();
        this.webClient = createWebClient();
    }
    
    @Bean
    public WebClient rateServiceClient() {
        return this.webClient;
    }
    
    /**
     * Refresh token every 12 hours to avoid expiry
     */
    @Scheduled(fixedRate = 12 * 60 * 60 * 1000) // 12 hours
    public void scheduledTokenRefresh() {
        logger.info("Refreshing service-to-service JWT token");
        refreshToken();
        this.webClient = createWebClient();
    }
    
    private void refreshToken() {
        // Get the system user from the repository
        Optional<User> serviceUser = userRepository.findByUsername(serviceUsername);
        
        if (serviceUser.isPresent()) {
            User user = serviceUser.get();
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, // account not expired
                true, // credentials not expired
                true, // account not locked
                Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
            );
            currentToken = jwtUtil.generateToken(userDetails);
            logger.debug("JWT token refreshed for service-to-service communication");
        } else {
            logger.error("Service user '{}' not found in database, cannot generate JWT token", serviceUsername);
            currentToken = null;
        }
    }
    
    private WebClient createWebClient() {
        WebClient.Builder builder = WebClient.builder().baseUrl(rateServiceBaseUrl);
        
        if (currentToken != null) {
            builder.defaultHeaders(headers -> headers.setBearerAuth(currentToken));
        } else {
            logger.warn("No JWT token available for service-to-service communication");
        }
        
        return builder.build();
    }
}