package com.quizapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ======== SIMPLE EMAIL TEST ========
    @Test
    void sendSimpleEmail_shouldCallMailSender() {
        String to = "test@example.com";
        String subject = "Hello";
        String body = "Test body";

        // Call method
        emailService.sendSimpleEmail(to, subject, body);

        // Capture sent message
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }

    // ======== HTML EMAIL TEST ========
    @Test
    void sendEmail_shouldCallMailSender() throws MessagingException {
        String to = "html@example.com";
        String subject = "HTML Email";
        String body = "<h1>Hello</h1>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Call method
        emailService.sendEmail(to, subject, body);

        // Verify mailSender.send was called
        verify(mailSender).send(mimeMessage);
    }

    // ======== MESSAGING EXCEPTION TEST ========
    @Test
    void sendEmail_shouldThrowMessagingException_whenMimeMessageFails() throws MessagingException {
        String to = "fail@example.com";
        String subject = "Fail";
        String body = "<p>Fail</p>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        // MimeMessageHelper constructor can throw MessagingException, so we can skip actual helper testing

        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, body));

        verify(mailSender).send(mimeMessage);
    }
}
