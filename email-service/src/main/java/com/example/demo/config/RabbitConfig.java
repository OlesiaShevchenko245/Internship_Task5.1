package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${mb.email.exchange}")
    private String exchangeName;

    @Value("${mb.email.queue}")
    private String queueName;

    @Value("${mb.email.routingKey}")
    private String routingKey;

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(routingKey);
    }
}
