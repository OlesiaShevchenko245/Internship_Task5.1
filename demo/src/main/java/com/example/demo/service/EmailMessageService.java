package com.example.demo.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.dto.EmailSendRequest;
import com.example.demo.model.EmailMessageDoc;
import com.example.demo.model.EmailStatus;
import com.example.demo.repository.EmailMessageRepository;

@Service
public class EmailMessageService {

    private final EmailMessageRepository repo;
    private final SmtpEmailSender sender;

    @Value("${email.from}")
    private String from;

    public EmailMessageService(EmailMessageRepository repo, SmtpEmailSender sender) {
        this.repo = repo;
        this.sender = sender;
    }

    public EmailMessageDoc createFromRequest(EmailSendRequest req) {
        EmailMessageDoc doc = new EmailMessageDoc();

        String id = (req.getEventId() != null && !req.getEventId().isBlank())
                ? req.getEventId()
                : UUID.randomUUID().toString();

        doc.setId(id);
        doc.setSourceService(req.getSourceService());
        doc.setTemplate(req.getTemplate());
        doc.setSubject(req.getSubject());
        doc.setContent(req.getContent());
        doc.setRecipients(req.getRecipients());

        doc.setStatus(EmailStatus.PENDING);
        doc.setAttempt(0);
        doc.setCreatedAt(req.getCreatedAt() != null ? req.getCreatedAt() : Instant.now());
        doc.setLastAttemptAt(null);
        doc.setErrorMessage(null);

        return repo.save(doc);
    }

    public EmailMessageDoc trySend(EmailMessageDoc doc) {
        if (doc.getRecipients() == null || doc.getRecipients().isEmpty()) {
            doc.setStatus(EmailStatus.ERROR);
            doc.setAttempt(doc.getAttempt() == null ? 1 : doc.getAttempt() + 1);
            doc.setLastAttemptAt(Instant.now());
            doc.setErrorMessage("IllegalState: recipients is empty");
            return repo.save(doc);
        }

        doc.setAttempt(doc.getAttempt() == null ? 1 : doc.getAttempt() + 1);
        doc.setLastAttemptAt(Instant.now());

        try {
            sender.send(from, doc.getRecipients(), doc.getSubject(), doc.getContent());

            doc.setStatus(EmailStatus.SENT);
            doc.setErrorMessage(null);
            return repo.save(doc);

        } catch (Exception ex) {
            doc.setStatus(EmailStatus.FAILED);
            doc.setErrorMessage(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            return repo.save(doc);
        }
    }
}
