package com.example.demo.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.model.EmailMessageDoc;
import com.example.demo.model.EmailStatus;
import com.example.demo.repository.EmailMessageRepository;
import com.example.demo.service.EmailMessageService;

@Component
public class EmailRetryScheduler {

    private final EmailMessageRepository repo;
    private final EmailMessageService service;

    public EmailRetryScheduler(EmailMessageRepository repo, EmailMessageService service) {
        this.repo = repo;
        this.service = service;
    }

    @Scheduled(fixedDelayString = "${email.retry.delay-ms:300000}")
    public void retryFailed() {
        List<EmailMessageDoc> failed = repo.findTop100ByStatusOrderByLastAttemptAtAsc(EmailStatus.FAILED);
        for (EmailMessageDoc doc : failed) {
            service.trySend(doc);
        }
    }
}
