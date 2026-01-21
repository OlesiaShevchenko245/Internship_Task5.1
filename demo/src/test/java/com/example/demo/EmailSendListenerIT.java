package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.time.Instant;
import java.util.List;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.dto.EmailSendRequest;
import com.example.demo.mb.EmailSendListener;
import com.example.demo.model.EmailMessageDoc;
import com.example.demo.model.EmailStatus;
import com.example.demo.repository.EmailMessageRepository;

@SpringBootTest
@ActiveProfiles("test")
class EmailSendListenerIT extends BaseElasticsearchIT {

    @Autowired
    private EmailSendListener listener;

    @Autowired
    private EmailMessageRepository repo;

    @MockBean
    private JavaMailSender mailSender;

    @BeforeEach
    void clean() {
        repo.deleteAll();
    }

    @Test
    void onMessage_whenSmtpOk_thenSavedAsSent() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        EmailSendRequest req = new EmailSendRequest();
        req.setEventId("it-ok-1");
        req.setSourceService("task2-observations-service");
        req.setTemplate("OBSERVATION_CREATED");
        req.setSubject("New observation created");
        req.setContent("Hello");
        req.setRecipients(List.of("a@b.com"));
        req.setCreatedAt(Instant.now());

        listener.onMessage(req);

        Awaitility.await().untilAsserted(() -> {
            EmailMessageDoc doc = repo.findById("it-ok-1").orElseThrow();
            assertThat(doc.getStatus()).isEqualTo(EmailStatus.SENT);
            assertThat(doc.getLastAttemptAt()).isNotNull();
        });
    }

    @Test
    void onMessage_whenSmtpFails_thenSavedAsFailedWithErrorMessage() {
        doThrow(new RuntimeException("smtp down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        EmailSendRequest req = new EmailSendRequest();
        req.setEventId("it-fail-1");
        req.setSourceService("task2-observations-service");
        req.setTemplate("OBSERVATION_CREATED");
        req.setSubject("New observation created");
        req.setContent("Hello");
        req.setRecipients(List.of("coffee245bean@gmail.com"));
        req.setCreatedAt(Instant.now());

        listener.onMessage(req);

        Awaitility.await().untilAsserted(() -> {
            EmailMessageDoc doc = repo.findById("it-fail-1").orElseThrow();
            assertThat(doc.getStatus()).isEqualTo(EmailStatus.FAILED);
            assertThat(doc.getErrorMessage()).contains("RuntimeException").contains("smtp down");
            assertThat(doc.getLastAttemptAt()).isNotNull();
        });
    }
}
