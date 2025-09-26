package com.quizapp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document(collection = "results")
public class Result {

    @Id
    private String id;

    private String userId;

    private String quizId;

    // Map questionId (or question index) -> selected option index
    private Map<String, Integer> answers;

    private int score;

    private Date attemptDate;

    public Result() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public Map<String, Integer> getAnswers() { return answers; }
    public void setAnswers(Map<String, Integer> answers) { this.answers = answers; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public Date getAttemptDate() { return attemptDate; }
    public void setAttemptDate(Date attemptDate) { this.attemptDate = attemptDate; }
}

