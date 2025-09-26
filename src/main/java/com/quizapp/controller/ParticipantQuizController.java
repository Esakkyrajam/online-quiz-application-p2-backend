package com.quizapp.controller;


import com.quizapp.dto.ResultDTO;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.Result;
import com.quizapp.dto.QuizSubmission;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.EmailService;
import com.quizapp.service.QuizService;
import com.quizapp.service.ResultService;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participant/quizzes")
@PreAuthorize("hasRole('PARTICIPANT')")
@CrossOrigin(origins = "https://onlinequizapplicationp2.netlify.app")
public class ParticipantQuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<List<Quiz>> listQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable String id) {
        return quizService.getQuizById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    @PostMapping("/{id}/submit")
//    public ResponseEntity<Result> submitQuiz(@PathVariable String id, @RequestBody QuizSubmission submission) {
//        // Perform grading, calculate score here or delegate to service
//        // Save attempt result and return it
//        try {
//            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//            String userEmail;
//            if (principal instanceof UserDetails) {
//                userEmail = ((UserDetails) principal).getUsername();
//            } else {
//                userEmail = principal.toString();
//            }
//
//// Assuming you can fetch User entity by email to get userId (or you can store it directly in JWT claims)
//            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
//
//
//            Result result = new Result();
//            result.setQuizId(id);
//            result.setUserId(user.getId());
//            // Set userId from security context (omitted here for brevity)
//            result.setAnswers(submission.getAnswers());
//            // Assume score calculated inside service or here - simplified:
//            int score = calculateScore(id, submission);
//            result.setScore(score);
//            Result saved = resultService.saveResult(result);
//
//
//            return ResponseEntity.ok(saved);
//        }catch (RuntimeException e) {
//            // Create a Result object for the error case
//            Result errorResult = new Result();
//            errorResult.setQuizId(id);
//            errorResult.setScore(-1); // Indicate error with a negative score
//            errorResult.setAnswers(Map.of("error", -1)); // Optionally store error info
//            return ResponseEntity.status(400).body(errorResult);
//        }
//    }


@PostMapping("/{id}/submit")
public ResponseEntity<Result> submitQuiz(@PathVariable String id, @RequestBody QuizSubmission submission) {
    try {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;
        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Result result = new Result();
        result.setQuizId(id);
        result.setUserId(user.getId());
        result.setAnswers(submission.getAnswers());
        int score = calculateScore(id, submission);
        result.setScore(score);
        Result saved = resultService.saveResult(result);

        return ResponseEntity.ok(saved);
    } catch (RuntimeException e) {
        Result errorResult = new Result();
        errorResult.setQuizId(id);
        errorResult.setScore(-1);
        errorResult.setAnswers(Map.of("error", -1));
        return ResponseEntity.status(400).body(errorResult);
    }
}

    private int calculateScore(String quizId, QuizSubmission submission) {
        Quiz quiz = quizService.getQuizById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int score = 0;
        List<Quiz.Question> questions = quiz.getQuestions();

        // Answers map has keys as String question indexes and Integer selected option indexes
        Map<String, Integer> answers = submission.getAnswers();

        for (int i = 0; i < questions.size(); i++) {
            Quiz.Question question = questions.get(i);
            Integer selectedOption = answers.get(String.valueOf(i));
            if (selectedOption != null && selectedOption == question.getCorrectIndex()) {
                score++;
            }
        }
        return score;
    }

    /** Get all results for the logged-in participant */
    @GetMapping("/results")
    public ResponseEntity<List<ResultDTO>> getMyResults() {
        User user = getLoggedInUser();
        List<ResultDTO> results = resultService.getResultsByUser(user.getId());
        return ResponseEntity.ok(results);
    }

    /** Helper to get currently logged-in user */
    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;
        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }



//    private int calculateScore(Quiz quiz, Map<String, Integer> submittedAnswers) {
//        int score = 0;
//        List<Quiz.Question> questions = quiz.getQuestions();
//
//        for (int i = 0; i < questions.size(); i++) {
//            Quiz.Question question = questions.get(i);
//            Integer selectedOption = submittedAnswers.get(String.valueOf(i));
//            if (selectedOption != null && selectedOption == question.getCorrectIndex()) {
//                score++;
//            }
//        }
//        return score;
//    }

}
