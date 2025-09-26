package com.quizapp.controller;

import com.quizapp.entity.User;
import com.quizapp.service.ResultService;
import com.quizapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ResultService resultService;

    @InjectMocks
    private AdminUserController adminUserController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user1");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRoles(Collections.singleton("ROLE_USER"));
    }

    // Test the getAllUsersWithResults endpoint
    @Test
    void testGetAllUsersWithResults() {
        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(resultService.countResultsByUserId("user1")).thenReturn(5);

        ResponseEntity<List<Map<String, Object>>> response = adminUserController.getAllUsersWithResults();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Map<String, Object>> userList = response.getBody();
        assertEquals(1, userList.size());
        Map<String, Object> userMap = userList.get(0);
        assertEquals("user1", userMap.get("id"));
        assertEquals("testuser", userMap.get("username"));
        assertEquals("test@example.com", userMap.get("email"));
        assertEquals(5, userMap.get("resultsCount"));
        verify(userService, times(1)).getAllUsers();
        verify(resultService, times(1)).countResultsByUserId("user1");
    }

    // Test the getUser endpoint when user is found
    @Test
    void testGetUserFound() {
        when(userService.getUserById("user1")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = adminUserController.getUser("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user1", response.getBody().getId());
        verify(userService, times(1)).getUserById("user1");
    }

    // Test the getUser endpoint when user is not found
    @Test
    void testGetUserNotFound() {
        when(userService.getUserById(anyString())).thenReturn(Optional.empty());

        ResponseEntity<User> response = adminUserController.getUser("nonexistentId");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById("nonexistentId");
    }

    // Test the updateUser endpoint
    @Test
    void testUpdateUser() {
        User updatedUser = new User();
        updatedUser.setId("user1");
        updatedUser.setUsername("updateduser");
        when(userService.updateUser(anyString(), any(User.class))).thenReturn(updatedUser);

        ResponseEntity<User> response = adminUserController.updateUser("user1", updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updateduser", response.getBody().getUsername());
        verify(userService, times(1)).updateUser("user1", updatedUser);
    }

    // Test the deleteUser endpoint
    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(anyString());

        ResponseEntity<?> response = adminUserController.deleteUser("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).deleteUser("user1");
    }
}