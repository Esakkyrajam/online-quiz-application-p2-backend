package com.quizapp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret = "Hh2f8G4k5Jk9Lw3M1Zq7R8xT9vU0yP6aB4cD3eF1gH2jK3lM4nO5pQ6rS7tU8vW9";
    private final long expiration = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Use reflection to inject secret and expiration values
        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, secret);

        Field expirationField = JwtService.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, expiration);
    }



    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        User userDetails = new User("testUser", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.validateToken(token, userDetails), "Token should be valid for correct user details");
    }

//    @Test
//    void validateToken_shouldReturnFalseForExpiredToken() throws Exception {
//        User userDetails = new User("testUser", "password",
//                List.of(new SimpleGrantedAuthority("ROLE_USER")));
//
//        // Set expiration negative to simulate an expired token
//        Field expirationField = JwtService.class.getDeclaredField("expiration");
//        expirationField.setAccessible(true);
//        expirationField.set(jwtService, -1000L);
//
//        String token = jwtService.generateToken(userDetails);
//        assertFalse(jwtService.validateToken(token, userDetails), "Token should be invalid if expired");
//    }

    @Test
    void validateToken_shouldReturnFalseForWrongUsername() {
        User userDetails = new User("testUser", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(userDetails);

        User otherUser = new User("otherUser", "password", List.of());
        assertFalse(jwtService.validateToken(token, otherUser), "Token should be invalid for mismatched username");
    }




}
