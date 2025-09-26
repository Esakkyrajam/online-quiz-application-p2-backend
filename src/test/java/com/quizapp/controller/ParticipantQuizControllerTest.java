package com.quizapp.controller;

import com.quizapp.dto.ResultDTO;
import com.quizapp.dto.QuizSubmission;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.Result;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.EmailService;
import com.quizapp.service.QuizService;
import com.quizapp.service.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ParticipantQuizControllerTest {

    @Mock
    private QuizService quizService;

    @Mock
    private ResultService resultService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ParticipantQuizController participantQuizController;

    private MockMvc mockMvc;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private User user;
    private Quiz quiz;
    private Result result;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(participantQuizController).build();

        user = new User();
        user.setId("user123");
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        quiz = new Quiz();
        quiz.setId("quiz123");
        quiz.setTitle("Test Quiz");
        Quiz.Question question = new Quiz.Question();
        question.setCorrectIndex(0);
        quiz.setQuestions(Collections.singletonList(question));

        result = new Result();
        result.setId("result123");
        result.setQuizId("quiz123");
        result.setUserId("user123");
        result.setScore(1);
        result.setAttemptDate(new Date());
        result.setAnswers(Map.of("0", 0));
    }

    @Test
    void testListQuizzes() throws Exception {
        List<Quiz> quizzes = Collections.singletonList(quiz);
        when(quizService.getAllQuizzes()).thenReturn(quizzes);

        mockMvc.perform(get("/api/participant/quizzes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("quiz123"))
                .andExpect(jsonPath("$[0].title").value("Test Quiz"));

        verify(quizService, times(1)).getAllQuizzes();
    }

    @Test
    void testGetQuizById_Success() throws Exception {
        when(quizService.getQuizById("quiz123")).thenReturn(Optional.of(quiz));

        mockMvc.perform(get("/api/participant/quizzes/quiz123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("quiz123"))
                .andExpect(jsonPath("$.title").value("Test Quiz"));

        verify(quizService, times(1)).getQuizById("quiz123");
    }

    @Test
    void testGetQuizById_NotFound() throws Exception {
        when(quizService.getQuizById("invalidId")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/participant/quizzes/invalidId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(quizService, times(1)).getQuizById("invalidId");
    }

    @Test
    void testSubmitQuiz_Success() throws Exception {
        QuizSubmission submission = new QuizSubmission();
        submission.setAnswers(Map.of("0", 0));

        // Setup security context for this test
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(quizService.getQuizById("quiz123")).thenReturn(Optional.of(quiz));
        when(resultService.saveResult(any(Result.class))).thenReturn(result);

        mockMvc.perform(post("/api/participant/quizzes/quiz123/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answers\": {\"0\": 0}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("result123"))
                .andExpect(jsonPath("$.score").value(1));

        verify(quizService, times(1)).getQuizById("quiz123");
        verify(resultService, times(1)).saveResult(any(Result.class));
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testSubmitQuiz_UserNotFound() throws Exception {
        QuizSubmission submission = new QuizSubmission();
        submission.setAnswers(Map.of("0", 0));

        // Setup security context for this test
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/participant/quizzes/quiz123/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answers\": {\"0\": 0}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.score").value(-1))
                .andExpect(jsonPath("$.quizId").value("quiz123"))
                .andExpect(jsonPath("$.answers.error").value(-1));

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetMyResults() throws Exception {
        ResultDTO resultDTO = new ResultDTO("result123", "quiz123", "Test Quiz", "user123", 1, new Date());

        // Setup security context for this test
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(resultService.getResultsByUser("user123")).thenReturn(Collections.singletonList(resultDTO));

        mockMvc.perform(get("/api/participant/quizzes/results")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("result123"))
                .andExpect(jsonPath("$[0].quizTitle").value("Test Quiz"))
                .andExpect(jsonPath("$[0].score").value(1));

        verify(resultService, times(1)).getResultsByUser("user123");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }
}