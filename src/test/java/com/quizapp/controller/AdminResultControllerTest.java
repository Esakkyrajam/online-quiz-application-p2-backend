package com.quizapp.controller;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.Result;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.QuizService;
import com.quizapp.service.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminResultControllerTest {

    @Mock
    private ResultService resultService;

    @Mock
    private QuizService quizService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminResultController adminResultController;

    private Result result1;
    private Result result2;
    private Quiz quiz1;
    private User user1;

    @BeforeEach
    void setUp() {
        // Setup Quiz data
        quiz1 = new Quiz();
        quiz1.setId("quiz1");
        quiz1.setTitle("Java Basics");
        quiz1.setQuestions(Arrays.asList(new Quiz.Question(), new Quiz.Question(), new Quiz.Question()));

        // Setup User data
        user1 = new User();
        user1.setId("user1");
        user1.setUsername("testuser");

        // Setup Result data
        result1 = new Result();
        result1.setId("result1");
        result1.setUserId("user1");
        result1.setQuizId("quiz1");
        result1.setScore(2);
        result1.setAttemptDate(new Date());

        result2 = new Result();
        result2.setId("result2");
        result2.setUserId("user2"); // A user that doesn't exist in our mock setup
        result2.setQuizId("quiz2"); // A quiz that doesn't exist
        result2.setScore(5);
        result2.setAttemptDate(new Date());
    }

    @Test
    void testGetAllResults() {
        // Mock the services' behavior
        when(resultService.getAllResults()).thenReturn(Arrays.asList(result1, result2));
        when(quizService.getQuizById("quiz1")).thenReturn(Optional.of(quiz1));
        when(quizService.getQuizById("quiz2")).thenReturn(Optional.empty()); // Mocking not found
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user2")).thenReturn(Optional.empty()); // Mocking not found

        // Call the controller method
        List<Map<String, Object>> results = adminResultController.getAllResults();

        // Assertions
        assertNotNull(results);
        assertEquals(2, results.size());

        // Assertions for the first result (fully resolved)
        Map<String, Object> map1 = results.get(0);
        assertEquals("result1", map1.get("id"));
        assertEquals(2, map1.get("score"));
        assertEquals("Java Basics", map1.get("quizTitle"));
        assertEquals(3, map1.get("totalQuestions"));
        assertEquals("testuser", map1.get("username"));
        assertNotNull(map1.get("attemptDate"));

        // Assertions for the second result (partially unresolved)
        Map<String, Object> map2 = results.get(1);
        assertEquals("result2", map2.get("id"));
        assertEquals(5, map2.get("score"));
        assertEquals("Unknown", map2.get("quizTitle")); // Verifies the "Unknown" fallback
        assertEquals(0, map2.get("totalQuestions")); // Verifies the 0 fallback
        assertEquals("Unknown", map2.get("username")); // Verifies the "Unknown" fallback
        assertNotNull(map2.get("attemptDate"));
    }
}