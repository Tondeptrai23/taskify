package com.taskify.iam.config;

import org.springframework.amqp.core.*;
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

    @Value("${rabbitmq.queue.iam-user-created-events}")
    private String iamUserCreatedEventsQueue;

    @Value("${rabbitmq.queue.iam-user-deleted-events}")
    private String iamUserDeletedEventsQueue;

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(userEventsExchange);
    }

    @Bean
    public Queue iamUserCreatedEventsQueue() {
        return new Queue(iamUserCreatedEventsQueue, true);
    }

    @Bean
    public Queue iamUserDeletedEventsQueue() {
        return new Queue(iamUserDeletedEventsQueue, true);
    }


    @Bean
    public Binding bindingUserEvents() {
        return BindingBuilder.bind(iamUserCreatedEventsQueue())
                .to(userEventsExchange())
                .with("user.created");
    }

    @Bean
    public Binding bindingUserDeletedEvents() {
        return BindingBuilder.bind(iamUserDeletedEventsQueue())
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
