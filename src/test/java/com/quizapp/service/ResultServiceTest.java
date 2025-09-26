package com.quizapp.service;

import com.quizapp.dto.ResultDTO;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.Result;
import com.quizapp.entity.User;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.ResultRepository;
import com.quizapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuizService quizService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ResultService resultService;

    private Result result;
    private User user;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("u1");
        user.setUsername("TestUser");
        user.setEmail("test@example.com");

        quiz = new Quiz();
        quiz.setId("q1");
        quiz.setTitle("Java Basics");
        quiz.setQuestions(List.of());

        result = new Result();
        result.setId("r1");
        result.setUserId(user.getId());
        result.setQuizId(quiz.getId());
        result.setScore(80);
    }

    @Test
    void saveResult_shouldSaveAndSendEmail() {
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(quizService.getQuizById(quiz.getId())).thenReturn(Optional.of(quiz));

        Result saved = resultService.saveResult(result);

        assertNotNull(saved.getAttemptDate());
        assertEquals(80, saved.getScore());

        // Verify email was sent
        verify(emailService, times(1))
                .sendSimpleEmail(eq(user.getEmail()), contains("Your Quiz Results"), anyString());
    }

    @Test
    void getAllResultsForUser_shouldReturnResults() {
        when(resultRepository.findByUserId(user.getId())).thenReturn(List.of(result));

        List<Result> results = resultService.getAllResultsForUser(user.getId());

        assertEquals(1, results.size());
        assertEquals(result.getId(), results.get(0).getId());
    }

    @Test
    void getResultById_shouldReturnResultOptional() {
        when(resultRepository.findById("r1")).thenReturn(Optional.of(result));

        Optional<Result> resultOpt = resultService.getResultById("r1");

        assertTrue(resultOpt.isPresent());
        assertEquals("r1", resultOpt.get().getId());
    }

    @Test
    void deleteResult_shouldCallRepositoryDelete() {
        doNothing().when(resultRepository).deleteById("r1");

        resultService.deleteResult("r1");

        verify(resultRepository, times(1)).deleteById("r1");
    }

    @Test
    void countResultsByUserId_shouldReturnCorrectCount() {
        when(resultRepository.findByUserId(user.getId())).thenReturn(List.of(result, result));

        int count = resultService.countResultsByUserId(user.getId());

        assertEquals(2, count);
    }

    @Test
    void getResultsByUser_shouldReturnDTOsWithQuizTitle() {
        when(resultRepository.findByUserId(user.getId())).thenReturn(List.of(result));
        when(quizService.getQuizById(quiz.getId())).thenReturn(Optional.of(quiz));

        List<ResultDTO> dtos = resultService.getResultsByUser(user.getId());

        assertEquals(1, dtos.size());
        assertEquals("Java Basics", dtos.get(0).getQuizTitle());
        assertEquals(result.getScore(), dtos.get(0).getScore());
    }

    @Test
    void getResultsByUser_shouldHandleMissingQuiz() {
        when(resultRepository.findByUserId(user.getId())).thenReturn(List.of(result));
        when(quizService.getQuizById(quiz.getId())).thenReturn(Optional.empty());

        List<ResultDTO> dtos = resultService.getResultsByUser(user.getId());

        assertEquals("Unknown Quiz", dtos.get(0).getQuizTitle());
    }
}
