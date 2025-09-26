package com.quizapp.controller;

import com.quizapp.dto.JwtResponse;
import com.quizapp.dto.LoginRequest;
import com.quizapp.dto.SignUpRequest;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.AuthService;
import com.quizapp.service.EmailService;
import com.quizapp.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private SignUpRequest signUpRequestParticipant;
    private SignUpRequest signUpRequestAdmin;
    private LoginRequest loginRequest;
    private AuthController.ForgotPasswordRequest forgotPasswordRequest;
    private AuthController.ResetPasswordRequest resetPasswordRequest;

    @BeforeEach
    void setup() {
        signUpRequestParticipant = new SignUpRequest("user", "user@example.com", "password");
        signUpRequestAdmin = new SignUpRequest("admin", "admin@company.com", "password");
        loginRequest = new LoginRequest("user@example.com", "password");

        forgotPasswordRequest = new AuthController.ForgotPasswordRequest();
        forgotPasswordRequest.setEmail("user@example.com");

        resetPasswordRequest = new AuthController.ResetPasswordRequest();
        resetPasswordRequest.setToken("validToken");
        resetPasswordRequest.setNewPassword("newPassword");
    }

    @Test
    void testRegisterUser_Success_Participant() {
        when(userRepository.existsByEmail(signUpRequestParticipant.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signUpRequestParticipant.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        ResponseEntity<?> response = authController.registerUser(signUpRequestParticipant);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully!", response.getBody());
        assertNotNull(signUpRequestParticipant.getEmail(), "SignUpRequest email should not be null");

        verify(userRepository).existsByEmail(signUpRequestParticipant.getEmail());
        verify(passwordEncoder).encode(signUpRequestParticipant.getPassword());
        verify(userRepository).save(any(User.class));
        verify(emailService).sendSimpleEmail(
                eq("user@example.com"),
                eq("Registration Confirmation from QuizApp Team"),
                anyString()
        );
        verifyNoInteractions(authService); // AuthService not used in register
    }

    @Test
    void testRegisterUser_Success_Admin() {
        when(userRepository.existsByEmail(signUpRequestAdmin.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signUpRequestAdmin.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        ResponseEntity<?> response = authController.registerUser(signUpRequestAdmin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully!", response.getBody());
        assertNotNull(signUpRequestAdmin.getEmail(), "SignUpRequest admin email should not be null");

        verify(userRepository).existsByEmail(signUpRequestAdmin.getEmail());
        verify(passwordEncoder).encode(signUpRequestAdmin.getPassword());
        verify(userRepository).save(any(User.class));
        verify(emailService).sendSimpleEmail(
                eq("admin@company.com"),
                eq("Registration Confirmation from QuizApp Team"),
                anyString()
        );
        verifyNoInteractions(authService); // AuthService not used in register
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(signUpRequestParticipant.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signUpRequestParticipant);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Email is already in use!", response.getBody());

        verify(userRepository).existsByEmail(signUpRequestParticipant.getEmail());
        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
        verifyNoInteractions(authService); // AuthService not used in register
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Invalid credentials") {});

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
        verifyNoInteractions(authService); // AuthService not used in login
    }

    @Test
    void testForgotPassword_Success() {
        when(authService.forgotPassword(anyString())).thenReturn(true);

        ResponseEntity<?> response = authController.forgotPassword(forgotPasswordRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset email sent!", response.getBody());
        verify(authService).forgotPassword(forgotPasswordRequest.getEmail());
    }

    @Test
    void testForgotPassword_UserNotFound() {
        when(authService.forgotPassword(anyString())).thenReturn(false);

        ResponseEntity<?> response = authController.forgotPassword(forgotPasswordRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found!", response.getBody());
        verify(authService).forgotPassword(forgotPasswordRequest.getEmail());
    }

    @Test
    void testResetPassword_Success() {
        when(authService.resetPassword(anyString(), anyString())).thenReturn(true);

        ResponseEntity<?> response = authController.resetPassword(resetPasswordRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password has been reset successfully!", response.getBody());
        verify(authService).resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
    }

    @Test
    void testResetPassword_InvalidToken() {
        when(authService.resetPassword(anyString(), anyString())).thenReturn(false);

        ResponseEntity<?> response = authController.resetPassword(resetPasswordRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid or expired token!", response.getBody());
        verify(authService).resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
    }
}