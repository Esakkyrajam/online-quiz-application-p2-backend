package com.quizapp.service;


import com.quizapp.entity.Quiz;
import com.quizapp.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizService quizService;

    private Quiz quiz1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        quiz1 = new Quiz();
        quiz1.setId("1");
        quiz1.setTitle("Java Basics");
        quiz1.setDescription("Test your Java knowledge");
        quiz1.setTimeLimitSeconds(600);
        quiz1.setQuestions(List.of()); // empty list for simplicity
    }

    @Test
    void createQuiz_shouldSaveAndReturnQuiz() {
        when(quizRepository.save(quiz1)).thenReturn(quiz1);

        Quiz saved = quizService.createQuiz(quiz1);

        assertEquals("Java Basics", saved.getTitle());
        verify(quizRepository, times(1)).save(quiz1);
    }

    @Test
    void getAllQuizzes_shouldReturnList() {
        when(quizRepository.findAll()).thenReturn(List.of(quiz1));

        List<Quiz> quizzes = quizService.getAllQuizzes();

        assertEquals(1, quizzes.size());
        assertEquals("Java Basics", quizzes.get(0).getTitle());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void getQuizById_shouldReturnOptionalQuiz() {
        when(quizRepository.findById("1")).thenReturn(Optional.of(quiz1));

        Optional<Quiz> result = quizService.getQuizById("1");

        assertTrue(result.isPresent());
        assertEquals("Java Basics", result.get().getTitle());
        verify(quizRepository, times(1)).findById("1");
    }

    @Test
    void updateQuiz_shouldUpdateExistingQuiz() {
        Quiz updatedQuiz = new Quiz();
        updatedQuiz.setTitle("Advanced Java");
        updatedQuiz.setDescription("Advanced topics");
        updatedQuiz.setTimeLimitSeconds(900);
        updatedQuiz.setQuestions(List.of());

        when(quizRepository.findById("1")).thenReturn(Optional.of(quiz1));
        when(quizRepository.save(any(Quiz.class))).thenAnswer(i -> i.getArguments()[0]);

        Quiz result = quizService.updateQuiz("1", updatedQuiz);

        assertEquals("Advanced Java", result.getTitle());
        assertEquals(900, result.getTimeLimitSeconds());
        verify(quizRepository, times(1)).findById("1");
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void updateQuiz_shouldThrowExceptionIfNotFound() {
        when(quizRepository.findById("2")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> quizService.updateQuiz("2", quiz1));

        assertEquals("Quiz not found", ex.getMessage());
        verify(quizRepository, times(1)).findById("2");
        verify(quizRepository, never()).save(any());
    }

    @Test
    void deleteQuiz_shouldCallRepositoryDelete() {
        doNothing().when(quizRepository).deleteById("1");

        quizService.deleteQuiz("1");

        verify(quizRepository, times(1)).deleteById("1");
    }

    @Test
    void getQuizTitleById_shouldReturnTitle() {
        when(quizRepository.findById("1")).thenReturn(Optional.of(quiz1));

        String title = quizService.getQuizTitleById("1");

        assertEquals("Java Basics", title);
        verify(quizRepository, times(1)).findById("1");
    }

    @Test
    void getQuizTitleById_shouldThrowExceptionIfNotFound() {
        when(quizRepository.findById("2")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> quizService.getQuizTitleById("2"));

        assertEquals("Quiz not found with id: 2", ex.getMessage());
    }
}
