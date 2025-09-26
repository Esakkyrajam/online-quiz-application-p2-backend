package com.quizapp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "quizzes")
public class Quiz {

    @Id
    private String id;

    private String title;

    private String description;

    private int timeLimitSeconds;




        private List<Question> questions;

    public Quiz() {}

    public Quiz(String number, String javaBasics, String s, List<Object> objects, Object o, Object o1) {
    }

    public Quiz(String id, String title, String description, int timeLimitSeconds, List<Question> questions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timeLimitSeconds = timeLimitSeconds;
        this.questions = questions;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTimeLimitSeconds() { return timeLimitSeconds; }
    public void setTimeLimitSeconds(int timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions;
    }


        // Embedded Question class
    public static class Question {

        private String text;

        private List<String> options;

        private int correctIndex;

        public Question() {}

        // Getters and setters
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }

        public int getCorrectIndex() { return correctIndex; }
        public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }

            public void setQuestionText(String s) {
            }
        }
}

