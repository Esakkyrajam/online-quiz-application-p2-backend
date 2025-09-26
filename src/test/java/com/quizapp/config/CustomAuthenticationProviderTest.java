package com.quizapp.config;

import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthenticationProviderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_withValidCredentials_returnsAuthentication() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of("ADMIN"));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        Authentication authRequest = new UsernamePasswordAuthenticationToken("test@example.com", "password");

        // Act
        Authentication result = provider.authenticate(authRequest);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getName());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void authenticate_withInvalidPassword_throwsBadCredentialsException() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of("ADMIN"));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        Authentication authRequest = new UsernamePasswordAuthenticationToken("test@example.com", "wrongPassword");

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> provider.authenticate(authRequest));
    }

    @Test
    void authenticate_withNonExistingUser_throwsBadCredentialsException() {
        // Arrange
        when(userRepository.findByEmail("nonexist@example.com")).thenReturn(Optional.empty());

        Authentication authRequest = new UsernamePasswordAuthenticationToken("nonexist@example.com", "password");

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> provider.authenticate(authRequest));
    }
}
