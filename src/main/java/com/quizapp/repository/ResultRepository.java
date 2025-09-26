package com.quizapp.repository;


import com.quizapp.entity.Result;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResultRepository extends MongoRepository<Result, String> {

    // Custom method to fetch all results by userId
    List<Result> findByUserId(String  userId);

    long countByUserId(String userId);

}
