package com.quizapp.controller;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.entity.Result;
import java.text.SimpleDateFormat;

import com.quizapp.repository.UserRepository;
import com.quizapp.service.QuizService;
import com.quizapp.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/results")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "https://onlinequizapplicationp2.netlify.app")
public class AdminResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Map<String, Object>> getAllResults() {
        return resultService.getAllResults().stream()
                // Skip results with null userId or quizId
                .filter(result -> result.getUserId() != null && result.getQuizId() != null)
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", result.getId());
                    map.put("score", result.getScore());

                    // Safely fetch quiz
                    Optional<Quiz> quizOpt = quizService.getQuizById(result.getQuizId());
                    map.put("totalQuestions", quizOpt.map(q -> q.getQuestions().size()).orElse(0));
                    map.put("quizTitle", quizOpt.map(Quiz::getTitle).orElse("Unknown"));

                    // Safely fetch user
                    Optional<User> userOpt = userRepository.findById(result.getUserId());
                    map.put("username", userOpt.map(User::getUsername).orElse("Unknown"));

// inside your map creation
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    map.put("attemptDate", result.getAttemptDate() != null ? formatter.format(result.getAttemptDate()) : "-");

                   // map.put("attemptDate", result.getAttemptDate());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
