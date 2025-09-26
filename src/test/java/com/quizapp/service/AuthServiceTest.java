package com.quizapp.service;

import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ======== REGISTER TESTS ========
    @Test
    void register_shouldSaveUser_whenEmailNotExists() {
        String email = "test@example.com";
        String username = "testUser";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.register(username, email, password);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(Set.of("PARTICIPANT"), user.getRoles());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenEmailExists() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register("testUser", email, "password"));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ======== FORGOT PASSWORD TESTS ========
    @Test
    void forgotPassword_shouldSendEmail_whenEmailExists() throws MessagingException {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.forgotPassword(email);

        assertNotNull(user.getResetToken());
        verify(emailService).sendEmail(eq(email), anyString(), contains(user.getResetToken()));
        verify(userRepository).save(user);
    }



    // ======== RESET PASSWORD TESTS ========
    @Test
    void resetPassword_shouldUpdatePassword_whenTokenValid() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword";

        User user = new User();
        user.setResetToken(token);

        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.resetPassword(token, newPassword);

        assertEquals("encodedNewPassword", user.getPassword());
        assertNull(user.getResetToken());
        verify(userRepository).save(user);
    }


}

