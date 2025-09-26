package com.quizapp.controller;

import com.quizapp.entity.Quiz;
import com.quizapp.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminQuizControllerTest {

    @Mock
    private QuizService quizService;

    @InjectMocks
    private AdminQuizController adminQuizController;

    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        quiz.setId("quiz1");
        quiz.setTitle("Test Quiz");
    }

    @Test
    void testCreateQuiz() {
        when(quizService.createQuiz(any(Quiz.class))).thenReturn(quiz);

        ResponseEntity<Quiz> response = adminQuizController.createQuiz(quiz);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("quiz1", response.getBody().getId());
        verify(quizService, times(1)).createQuiz(any(Quiz.class));
    }

    @Test
    void testGetAllQuizzes() {
        List<Quiz> quizzes = Arrays.asList(quiz, new Quiz());
        when(quizService.getAllQuizzes()).thenReturn(quizzes);

        ResponseEntity<List<Quiz>> response = adminQuizController.getAllQuizzes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(quizService, times(1)).getAllQuizzes();
    }

    @Test
    void testGetQuizFound() {
        when(quizService.getQuizById("quiz1")).thenReturn(Optional.of(quiz));

        ResponseEntity<Quiz> response = adminQuizController.getQuiz("quiz1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("quiz1", response.getBody().getId());
        verify(quizService, times(1)).getQuizById("quiz1");
    }

    @Test
    void testGetQuizNotFound() {
        when(quizService.getQuizById(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Quiz> response = adminQuizController.getQuiz("nonexistentId");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(quizService, times(1)).getQuizById("nonexistentId");
    }

    @Test
    void testUpdateQuiz() {
        Quiz updatedQuiz = new Quiz();
        updatedQuiz.setId("quiz1");
        updatedQuiz.setTitle("Updated Title");

        when(quizService.updateQuiz(anyString(), any(Quiz.class))).thenReturn(updatedQuiz);

        ResponseEntity<Quiz> response = adminQuizController.updateQuiz("quiz1", quiz);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Title", response.getBody().getTitle());
        verify(quizService, times(1)).updateQuiz("quiz1", quiz);
    }

    @Test
    void testDeleteQuiz() {
        doNothing().when(quizService).deleteQuiz(anyString());

        ResponseEntity<?> response = adminQuizController.deleteQuiz("quiz1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(quizService, times(1)).deleteQuiz("quiz1");
    }
}