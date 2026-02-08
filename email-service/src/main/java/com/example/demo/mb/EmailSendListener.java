package com.example.demo.mb;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.demo.dto.EmailSendRequest;
import com.example.demo.model.EmailMessageDoc;
import com.example.demo.service.EmailMessageService;

@Component
public class EmailSendListener {

    private final EmailMessageService service;

    public EmailSendListener(EmailMessageService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${mb.email.queue}")
    public void onMessage(EmailSendRequest request) {
        EmailMessageDoc saved = service.createFromRequest(request);
        service.trySend(saved);
    }
}
