package com.quizapp.dto;

import java.util.Date;

public class ResultDTO {
    private String id;         // Result ID
    private String quizId;     // Quiz ID
    private String quizTitle;  // Quiz Title
    private String userId;       // User ID
    private int score;         // Score
    private Date attemptDate;  // Date of attempt

    public ResultDTO() {}

    public ResultDTO(String id, String quizId, String quizTitle, String userId, int score, Date attemptDate) {
        this.id = id;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.userId = userId;
        this.score = score;
        this.attemptDate = attemptDate;
    }

    public ResultDTO(String s, String s1, int i) {
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public Date getAttemptDate() { return attemptDate; }
    public void setAttemptDate(Date attemptDate) { this.attemptDate = attemptDate; }
}
