//package com.quizapp.service;
//
//
//import com.quizapp.entity.Result;
//import com.quizapp.repository.ResultRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ResultService {
//
//    @Autowired
//    private ResultRepository resultRepository;
//
//
//
//    public Result saveResult(Result result) {
//        result.setAttemptDate(new Date());
//
//
//        return resultRepository.save(result);
//    }
//
//    public List<Result> getAllResultsForUser(String userId) {
//        return resultRepository.findByUserId(userId);
//    }
//
//    public Optional<Result> getResultById(String id) {
//        return resultRepository.findById(id);
//    }
//
//    public void deleteResult(String id) {
//        resultRepository.deleteById(id);
//    }
//}


package com.quizapp.service;

import com.quizapp.dto.ResultDTO;
import com.quizapp.entity.Result;
import com.quizapp.entity.User;
import com.quizapp.entity.Quiz;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.ResultRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private EmailService emailService;

    public Result saveResult(Result result) {
        // Set attempt date
        result.setAttemptDate(new Date());

        // Save the result
        Result savedResult = resultRepository.save(result);

        // Fetch User
        Optional<User> userOpt = userRepository.findById(result.getUserId());
        // Fetch Quiz
        Optional<Quiz> quizOpt = quizService.getQuizById(result.getQuizId());

        if (userOpt.isPresent() && quizOpt.isPresent()) {
            User user = userOpt.get();
            Quiz quiz = quizOpt.get();

            // Prepare email content
            String subject = "Your Quiz Results: " + quiz.getTitle();
            String body = "Hi " + user.getUsername() + ",\n\n"
                    + "Thank you for participating in the quiz: \"" + quiz.getTitle() + "\".\n"
                    + "Your score is: " + result.getScore() + " out of " + quiz.getQuestions().size() + ".\n\n"
                    + "We hope you enjoyed the quiz. Keep practicing and come back for more challenges!\n\n"
                    + "Best regards,\n"
                    + "The QuizApp Team";


            // Send email notification
            emailService.sendSimpleEmail(user.getEmail(), subject, body);
        }

        return savedResult;
    }

    public List<Result> getAllResultsForUser(String userId) {
        return resultRepository.findByUserId(userId);
    }

    public Optional<Result> getResultById(String id) {
        return resultRepository.findById(id);
    }

    public void deleteResult(String id) {
        resultRepository.deleteById(id);
    }

    public int countResultsByUserId(String userId) {
        return resultRepository.findByUserId(userId).size(); // Count all results of a use

    }


    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }


    /** Get all results for a specific user as DTOs with quiz title */
    public List<ResultDTO> getResultsByUser(String userId) {
        List<Result> results = resultRepository.findByUserId(userId);

        return results.stream().map(r -> {
            Quiz quiz = quizService.getQuizById(r.getQuizId())
                    .orElse(null);
            String quizTitle = quiz != null ? quiz.getTitle() : "Unknown Quiz";

            return new ResultDTO(
                    r.getId(),
                    r.getQuizId(),
                    quizTitle,
                    r.getUserId(),
                    r.getScore(),
                    r.getAttemptDate()
            );
        }).collect(Collectors.toList());
    }

}
