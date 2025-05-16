package com.example.mainservice.service;

import com.example.mainservice.model.User;
import com.example.mainservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Test
    void createUser_shouldReturnUser() {
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(repo.findByUsername("foo")).thenReturn(Optional.empty());
        when(encoder.encode(any())).thenReturn("encoded");
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        UserService service = new UserService(repo, encoder);
        User user = service.createUser("foo", "bar", "ROLE_USER");
        assertEquals("foo", user.getUsername());
        assertEquals("encoded", user.getPassword());
        assertEquals("ROLE_USER", user.getRole());
    }

    @Test
    void createUser_shouldThrowOnDuplicateUsername() {
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(repo.findByUsername("foo")).thenReturn(Optional.of(new User()));
        UserService service = new UserService(repo, encoder);
        assertThrows(IllegalArgumentException.class, () -> service.createUser("foo", "bar", "ROLE_USER"));
    }

    @Test
    void createUser_shouldThrowOnInvalidRole() {
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(repo.findByUsername("foo")).thenReturn(Optional.empty());
        UserService service = new UserService(repo, encoder);
        assertThrows(IllegalArgumentException.class, () -> service.createUser("foo", "bar", "INVALID_ROLE"));
    }
}
