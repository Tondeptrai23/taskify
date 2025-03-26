package com.taskify.auth.infrastructure.config;

import com.taskify.commoncore.event.EventConstants;
import org.springframework.amqp.core.TopicExchange;
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