package com.quizapp.service;

import com.quizapp.dto.SignUpRequest;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("u1");
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of("PARTICIPANT"));
    }

    @Test
    void registerUser_shouldAssignParticipantRole_forNonCompanyEmail() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testUser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(request);

        assertEquals("testUser", saved.getUsername());
        assertEquals("test@example.com", saved.getEmail());
        assertEquals("encodedPassword", saved.getPassword());
        assertTrue(saved.getRoles().contains("PARTICIPANT"));
    }

    @Test
    void registerUser_shouldAssignAdminRole_forCompanyEmail() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("adminUser");
        request.setEmail("admin@company.com");
        request.setPassword("adminPass");

        when(passwordEncoder.encode("adminPass")).thenReturn("encodedAdminPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(request);

        assertTrue(saved.getRoles().contains("ADMIN"));
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(user.getUsername(), users.get(0).getUsername());
    }

    @Test
    void getUserById_shouldReturnOptional() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById("u1");

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void updateUser_shouldModifyUserFields() {
        User updatedUser = new User();
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("newPass");
        updatedUser.setRoles(Set.of("ADMIN"));

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.updateUser("u1", updatedUser);

        assertEquals("updatedUser", saved.getUsername());
        assertEquals("updated@example.com", saved.getEmail());
        assertEquals("encodedNewPass", saved.getPassword());
        assertTrue(saved.getRoles().contains("ADMIN"));
    }

    @Test
    void deleteUser_shouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById("u1");

        userService.deleteUser("u1");

        verify(userRepository, times(1)).deleteById("u1");
    }

    @Test
    void sendPasswordResetEmail_shouldCallEmailService() throws MessagingException {
        // Use doNothing() for void methods
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        userService.sendPasswordResetEmail(user, "token123");

        // Verify it was called correctly
        verify(emailService, times(1)).sendEmail(
                eq(user.getEmail()),
                eq("Password Reset Request"),
                contains("token123")
        );
    }

}
