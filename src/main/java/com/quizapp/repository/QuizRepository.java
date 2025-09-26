package com.quizapp.repository;


import com.quizapp.entity.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizRepository extends MongoRepository<Quiz, String> {
    // Add custom query methods here as needed
}

