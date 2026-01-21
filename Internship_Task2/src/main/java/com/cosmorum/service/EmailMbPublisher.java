package com.cosmorum.service;

import com.cosmorum.dto.EmailSendRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailMbPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mb.email.exchange}")
    private String exchange;

    @Value("${mb.email.routingKey}")
    private String routingKey;

    public EmailMbPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(EmailSendRequest req) {
        rabbitTemplate.convertAndSend(exchange, routingKey, req);
    }
}
