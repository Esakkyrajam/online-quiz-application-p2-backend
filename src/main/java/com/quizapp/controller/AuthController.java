//package com.quizapp.controller;
//
//import com.quizapp.entity.User;
//import com.quizapp.dto.JwtResponse;
//import com.quizapp.dto.LoginRequest;
//import com.quizapp.dto.SignUpRequest;
//import com.quizapp.repository.UserRepository;
//import com.quizapp.service.AuthService;
//import com.quizapp.service.EmailService;
//import com.quizapp.service.JwtService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthenticationManager authenticationManager;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//
//    @Autowired
//    private EmailService emailService;
//
//
//    @Autowired
//    private AuthService authService;
//
//    // User Registration
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            return ResponseEntity.badRequest().body("Error: Email is already in use!");
//        }
//
//        // Create new user account
//        User user = new User();
//        user.setUsername(signUpRequest.getUsername());
//        user.setEmail(signUpRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
//
//        // Assign role based on email domain
//        if (user.getEmail().toLowerCase().endsWith("@company.com")) {
//            user.setRoles(Set.of("ADMIN"));
//        } else {
//            user.setRoles(Set.of("PARTICIPANT"));
//        }
//
//        userRepository.save(user);
//
//        // Send email confirmation
//        String body = "Hello " + signUpRequest.getUsername() + ",\n\n"
//                + "Thank you for registering with QuizApp. Your registration was successful.\n"
//                + "You can now log in and start participating in quizzes.\n\n"
//                + "Best regards,\n"
//                + "QuizApp Team";
//        emailService.sendSimpleEmail(signUpRequest.getEmail(), "Registration Confirmation from QuizApp Team", body);
//
//        return ResponseEntity.ok("User registered successfully!");
//    }
//
//    // User Login
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
//        );
//
//        var userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
//
//        // Generate JWT token, including roles
//        String jwt = jwtService.generateToken(userDetails);
//
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
//    }
//
//
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
//        authService.forgotPassword(request.getEmail());
//        return ResponseEntity.ok("Password reset email sent!");
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
//        authService.resetPassword(request.getToken(), request.getNewPassword());
//        return ResponseEntity.ok("Password has been reset successfully!");
//    }
//
//    // DTO classes
//    public static class ForgotPasswordRequest {
//        private String email;
//        public String getEmail() { return email; }
//        public void setEmail(String email) { this.email = email; }
//    }
//
//    public static class ResetPasswordRequest {
//        private String token;
//        private String newPassword;
//        public String getToken() { return token; }
//        public void setToken(String token) { this.token = token; }
//        public String getNewPassword() { return newPassword; }
//        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
//    }
//}
package com.quizapp.controller;

import com.quizapp.dto.JwtResponse;
import com.quizapp.dto.LoginRequest;
import com.quizapp.dto.SignUpRequest;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.AuthService;
import com.quizapp.service.EmailService;
import com.quizapp.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        if (user.getEmail().toLowerCase().endsWith("@company.com")) {
            user.setRoles(Set.of("ADMIN"));
        } else {
            user.setRoles(Set.of("PARTICIPANT"));
        }

        userRepository.save(user);

        String body = "Hello " + signUpRequest.getUsername() + ",\n\n"
                + "Thank you for registering with QuizApp. Your registration was successful.\n"
                + "You can now log in and start participating in quizzes.\n\n"
                + "Best regards,\n"
                + "QuizApp Team";
        emailService.sendSimpleEmail(signUpRequest.getEmail(), "Registration Confirmation from QuizApp Team", body);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            var userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            String jwt = jwtService.generateToken(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        boolean success = authService.forgotPassword(request.getEmail());
        if (success) {
            return ResponseEntity.ok("Password reset email sent!");
        } else {
            return ResponseEntity.badRequest().body("User not found!");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = authService.resetPassword(request.getToken(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok("Password has been reset successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token!");
        }
    }

    public static class ForgotPasswordRequest {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}