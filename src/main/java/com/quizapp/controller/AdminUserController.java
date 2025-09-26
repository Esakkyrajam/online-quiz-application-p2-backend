package com.quizapp.controller;

import com.quizapp.entity.Result;
import com.quizapp.entity.User;
import com.quizapp.service.ResultService;
import com.quizapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "https://onlinequizapplicationp2.netlify.app")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResultService resultService;

    // Fetch all users with their quiz results count
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsersWithResults() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> resultList = users.stream().map(user -> {
            int resultCount = resultService.countResultsByUserId(user.getId());
            return Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "roles", user.getRoles(),
                    "resultsCount", resultCount
            );
        }).toList();
        return ResponseEntity.ok(resultList);
    }

    // Get single user
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
