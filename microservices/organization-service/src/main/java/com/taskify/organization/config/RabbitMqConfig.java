package com.taskify.organization.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange.user-events}")
    private String userEventsExchange;

    @Value("${rabbitmq.queue.org-user-created-events}")
    private String orgUserCreatedEventsQueue;

    @Value("${rabbitmq.queue.org-user-deleted-events}")
    private String orgUserDeletedEventsQueue;

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(userEventsExchange);
    }

    @Bean
    public Queue orgUserCreatedEventsQueue() {
        return new Queue(orgUserCreatedEventsQueue, true);
    }

    @Bean
    public Queue orgUserDeletedEventsQueue() {
        return new Queue(orgUserDeletedEventsQueue, true);
    }

    @Bean
    public Binding bindingUserCreatedEvents() {
        return BindingBuilder.bind(orgUserCreatedEventsQueue())
                .to(userEventsExchange())
                .with("user.created");
    }

    @Bean
    public Binding bindingUserDeletedEvents() {
        return BindingBuilder.bind(orgUserDeletedEventsQueue())
                .to(userEventsExchange())
                .with("user.deleted");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}