package com.cosmorum.dto;

import java.time.Instant;
import java.util.List;

public class EmailSendRequest {

    private String eventId;
    private String sourceService;
    private String template;

    private String subject;
    private String content;
    private List<String> recipients;

    private Instant createdAt;

    public EmailSendRequest() {
    }

    public EmailSendRequest(
            String eventId,
            String sourceService,
            String template,
            String subject,
            String content,
            List<String> recipients,
            Instant createdAt) {
        this.eventId = eventId;
        this.sourceService = sourceService;
        this.template = template;
        this.subject = subject;
        this.content = content;
        this.recipients = recipients;
        this.createdAt = createdAt;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSourceService() {
        return sourceService;
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
