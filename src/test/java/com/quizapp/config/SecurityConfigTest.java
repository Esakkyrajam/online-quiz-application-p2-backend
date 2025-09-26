package com.quizapp.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SecurityConfigTest {

    @Mock
    private CustomAuthenticationProvider authenticationProvider;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private AuthenticationManagerBuilder authManagerBuilder;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticationManager_creation() throws Exception {
        // Mock HttpSecurity to return AuthenticationManagerBuilder
        when(httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)).thenReturn(authManagerBuilder);

        // Mock authManagerBuilder behavior to build AuthenticationManager
        when(authManagerBuilder.authenticationProvider(authenticationProvider)).thenReturn(authManagerBuilder);
        when(authManagerBuilder.build()).thenReturn(mock(AuthenticationManager.class));

        AuthenticationManager authenticationManager = securityConfig.authenticationManager(httpSecurity);
        assertNotNull(authenticationManager);

        verify(httpSecurity).getSharedObject(AuthenticationManagerBuilder.class);
        verify(authManagerBuilder).authenticationProvider(authenticationProvider);
        verify(authManagerBuilder).build();
    }

    @Test
    public void testSecurityFilterChain_configuration() throws Exception {
        // Here we mock chained method calls on HttpSecurity for fluent API
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(eq(jwtAuthenticationFilter), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mock(org.springframework.security.web.DefaultSecurityFilterChain.class));

        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);
        assertNotNull(filterChain);

        verify(httpSecurity).csrf(any());
        verify(httpSecurity).cors(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).addFilterBefore(eq(jwtAuthenticationFilter), any());
        verify(httpSecurity).build();
    }

    @Test
    public void testCorsConfigurationSource() {
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        assertNotNull(corsConfigurationSource);

        // Additional checks could be made by casting to UrlBasedCorsConfigurationSource
        // and inspecting its registered configurations if needed.
    }
}
