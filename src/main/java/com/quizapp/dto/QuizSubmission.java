package com.quizapp.dto;


import java.util.Map;

public class QuizSubmission {

    // Key: questionId (or question index as String), Value: chosen option index
    private Map<String, Integer> answers;

    public QuizSubmission() {
    }

    public Map<String, Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Integer> answers) {
        this.answers = answers;
    }
}

