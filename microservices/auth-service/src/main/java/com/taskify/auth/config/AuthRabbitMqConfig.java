package com.taskify.auth.config;

import com.taskify.commoncore.event.EventConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({com.taskify.commonweb.config.CommonRabbitMqConfig.class})
public class AuthRabbitMqConfig {

    private final EventConstants eventConstants;

    public AuthRabbitMqConfig(EventConstants eventConstants) {
        this.eventConstants = eventConstants;
    }

    // Exchanges
    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(eventConstants.getUserEventsExchange());
    }
}