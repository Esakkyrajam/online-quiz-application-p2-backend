package com.quizapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityBeansConfigTest {

    private final SecurityBeansConfig config = new SecurityBeansConfig();

    @Test
    void passwordEncoderBean_shouldReturnBCryptPasswordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder, "PasswordEncoder bean should not be null");
        assertTrue(encoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder,
                "PasswordEncoder should be an instance of BCryptPasswordEncoder");

        // Verify it can encode and match passwords
        String rawPassword = "secret123";
        String encoded = encoder.encode(rawPassword);

        assertNotEquals(rawPassword, encoded, "Encoded password should not match raw password");
        assertTrue(encoder.matches(rawPassword, encoded), "PasswordEncoder should validate encoded password");
    }
}
