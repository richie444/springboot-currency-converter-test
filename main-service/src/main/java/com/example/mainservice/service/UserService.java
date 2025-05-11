package com.example.mainservice.service;

import com.example.mainservice.model.User;
import com.example.mainservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(String username, String password, String role) {
        // Check if user already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        // Validate role
        validateRole(role);
        
        // Create and save new user with encrypted password
        User user = new User(username, passwordEncoder.encode(password), role);
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional
    public boolean changePassword(String username, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    @Transactional
    public boolean updateUserStatus(String username, boolean enabled) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(enabled);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    @Transactional
    public boolean updateUserRole(String username, String newRole) {
        // Validate role
        validateRole(newRole);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    @Transactional
    public boolean deleteUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            return true;
        }
        return false;
    }
    
    private void validateRole(String role) {
        if (role == null || (!role.equals("ROLE_USER") && !role.equals("ROLE_ADMIN"))) {
            throw new IllegalArgumentException("Invalid role. Valid roles are: ROLE_USER, ROLE_ADMIN");
        }
    }
}