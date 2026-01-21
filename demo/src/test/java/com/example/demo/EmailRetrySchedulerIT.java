package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.model.EmailMessageDoc;
import com.example.demo.model.EmailStatus;
import com.example.demo.repository.EmailMessageRepository;
import com.example.demo.scheduler.EmailRetryScheduler;
import com.example.demo.service.SmtpEmailSender;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = { "email.from=no-reply@test.com" })
@ActiveProfiles("test")
class EmailRetrySchedulerIT {

    @MockBean
    private EmailMessageRepository repo;

    @MockBean
    private SmtpEmailSender sender;

    @Autowired
    private EmailRetryScheduler scheduler;

    @Test
    void retryFailed_whenNowOk_shouldMarkSentAndIncrementAttempt() {
        // given
        EmailMessageDoc failed = new EmailMessageDoc();
        failed.setId("it-retry-1");
        failed.setSourceService("task2-observations-service");
        failed.setTemplate("OBSERVATION_CREATED");
        failed.setSubject("subj");
        failed.setContent("content");
        failed.setRecipients(List.of("a@b.com"));

        failed.setStatus(EmailStatus.FAILED);
        failed.setAttempt(0);
        failed.setCreatedAt(Instant.now().minus(10, ChronoUnit.MINUTES));
        failed.setLastAttemptAt(Instant.now().minus(6, ChronoUnit.MINUTES));
        failed.setErrorMessage("init");

        when(repo.findTop100ByStatusOrderByLastAttemptAtAsc(EmailStatus.FAILED))
                .thenReturn(List.of(failed));

        when(repo.save(any(EmailMessageDoc.class))).thenAnswer(inv -> inv.getArgument(0));

        doNothing().when(sender).send(any(), any(), any(), any());

        // when
        scheduler.retryFailed();

        // then
        ArgumentCaptor<EmailMessageDoc> savedCaptor = ArgumentCaptor.forClass(EmailMessageDoc.class);
        verify(repo, atLeastOnce()).save(savedCaptor.capture());

        EmailMessageDoc after = savedCaptor.getAllValues().get(savedCaptor.getAllValues().size() - 1);
        assertThat(after.getId()).isEqualTo("it-retry-1");
        assertThat(after.getStatus()).isEqualTo(EmailStatus.SENT);
        assertThat(after.getAttempt()).isEqualTo(1);
        assertThat(after.getLastAttemptAt()).isNotNull();
    }

    @Test
    void retryFailed_whenStillFail_shouldKeepFailedUpdateErrorAndIncrementAttempt() {
        // given
        EmailMessageDoc failed = new EmailMessageDoc();
        failed.setId("it-retry-2");
        failed.setSourceService("task2-observations-service");
        failed.setTemplate("OBSERVATION_CREATED");
        failed.setSubject("subj");
        failed.setContent("content");
        failed.setRecipients(List.of("a@b.com"));

        failed.setStatus(EmailStatus.FAILED);
        failed.setAttempt(1);
        failed.setCreatedAt(Instant.now().minus(10, ChronoUnit.MINUTES));
        failed.setLastAttemptAt(Instant.now().minus(6, ChronoUnit.MINUTES));
        failed.setErrorMessage("prev");

        when(repo.findTop100ByStatusOrderByLastAttemptAtAsc(EmailStatus.FAILED))
                .thenReturn(List.of(failed));

        when(repo.save(any(EmailMessageDoc.class))).thenAnswer(inv -> inv.getArgument(0));

        doThrow(new RuntimeException("still down"))
                .when(sender).send(any(), any(), any(), any());

        // when
        scheduler.retryFailed();

        // then
        ArgumentCaptor<EmailMessageDoc> savedCaptor = ArgumentCaptor.forClass(EmailMessageDoc.class);
        verify(repo, atLeastOnce()).save(savedCaptor.capture());

        EmailMessageDoc after = savedCaptor.getAllValues().get(savedCaptor.getAllValues().size() - 1);
        assertThat(after.getId()).isEqualTo("it-retry-2");
        assertThat(after.getStatus()).isEqualTo(EmailStatus.FAILED);
        assertThat(after.getAttempt()).isEqualTo(2);
        assertThat(after.getErrorMessage()).contains("RuntimeException").contains("still down");
        assertThat(after.getLastAttemptAt()).isNotNull();
    }
}
