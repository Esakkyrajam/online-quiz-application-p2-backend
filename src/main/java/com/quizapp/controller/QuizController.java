package com.quizapp.controller;

import com.quizapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "https://onlinequizapplicationp2.netlify.app")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/{id}/title")
    public ResponseEntity<String> getQuizTitle(@PathVariable String id) {
        String title = quizService.getQuizTitleById(id);
        return ResponseEntity.ok(title);
    }
}
