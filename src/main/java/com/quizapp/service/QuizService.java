package com.quizapp.service;


import com.quizapp.entity.Quiz;
import com.quizapp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(String id) {
        return quizRepository.findById(id);
    }

    public Quiz updateQuiz(String id, Quiz updatedQuiz) {
        return quizRepository.findById(id).map(quiz -> {
            quiz.setTitle(updatedQuiz.getTitle());
            quiz.setDescription(updatedQuiz.getDescription());
            quiz.setTimeLimitSeconds(updatedQuiz.getTimeLimitSeconds());
            quiz.setQuestions(updatedQuiz.getQuestions());
            return quizRepository.save(quiz);
        }).orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public void deleteQuiz(String id) {
        quizRepository.deleteById(id);
    }

    public String getQuizTitleById(String quizId) {
        return getQuizById(quizId)
                .map(Quiz::getTitle)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
    }


}
