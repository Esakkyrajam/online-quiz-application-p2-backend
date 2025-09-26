//package com.quizapp.service;
//
//import com.quizapp.entity.User;
//import com.quizapp.repository.UserRepository;
//import jakarta.mail.MessagingException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Set;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final EmailService emailService;
//
//    // Registration
//    public User register(String username, String email, String password) {
//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        User user = new User();
//        user.setUsername(username);
//        user.setEmail(email);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setRoles(Set.of("PARTICIPANT"));
//        return userRepository.save(user);
//    }
//
//    // Password reset request
//    public void forgotPassword(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Email not found"));
//
//        String token = UUID.randomUUID().toString();
//        user.setResetToken(token);
//        userRepository.save(user);
//
//        String resetLink = "http://localhost:5173/reset-password?token=" + token;
//        try {
//            emailService.sendEmail(user.getEmail(), "Password Reset Request",
//                    "Click here to reset your password: " + resetLink);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // Reset password
//    public void resetPassword(String token, String newPassword) {
//        User user = userRepository.findByResetToken(token)
//                .orElseThrow(() -> new RuntimeException("Invalid token"));
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        user.setResetToken(null); // clear token
//        userRepository.save(user);
//    }
//    private String generateResetToken() {
//        return "generated-token"; // Simplified for example
//    }
//
//    private String encodePassword(String password) {
//        return "encoded-" + password; // Simplified for example
//    }
//}



package com.quizapp.service;

import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Registration
    public User register(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of("PARTICIPANT"));
        return userRepository.save(user);
    }

    // Password reset request
    public boolean forgotPassword(String email) {
        try {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return false;
            }
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            userRepository.save(user);

            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            emailService.sendEmail(user.getEmail(), "Password Reset Request",
                    "Click here to reset your password: " + resetLink);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    // Reset password
    public boolean resetPassword(String token, String newPassword) {
        try {
            User user = userRepository.findByResetToken(token).orElse(null);
            if (user == null) {
                return false;
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}