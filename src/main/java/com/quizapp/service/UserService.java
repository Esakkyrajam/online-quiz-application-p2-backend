package com.quizapp.service;

import com.quizapp.entity.User;
import com.quizapp.dto.SignUpRequest;
import com.quizapp.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    // Register new user with role assignment based on email domain
    public User registerUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        if (user.getEmail().toLowerCase().endsWith("@company.com")) {
            user.setRoles(Set.of("ADMIN"));
        } else {
            user.setRoles(Set.of("PARTICIPANT"));
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User updateUser(String id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            if(updatedUser.getPassword() != null){
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            user.setRoles(updatedUser.getRoles());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public void sendPasswordResetEmail(User user, String token) throws MessagingException {
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        String htmlBody = "<p>Hello " + user.getUsername() + ",</p>"
                + "<p>You requested a password reset. Click the link below to reset your password:</p>"
                + "<a href=\"" + resetLink + "\">Reset Password</a>"
                + "<p>If you did not request this, ignore this email.</p>";

        emailService.sendEmail(user.getEmail(), "Password Reset Request", htmlBody);
    }
}

