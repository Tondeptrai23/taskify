package com.taskify.project.config;

import com.taskify.commoncore.event.EventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({com.taskify.commonweb.config.CommonRabbitMqConfig.class})
public class ProjectRabbitMqConfig {

    private final EventConstants eventConstants;

    public ProjectRabbitMqConfig(EventConstants eventConstants) {
        this.eventConstants = eventConstants;
    }

    // Exchanges
    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(eventConstants.getUserEventsExchange());
    }

    @Bean
    public TopicExchange projectEventsExchange() {
        return new TopicExchange(eventConstants.getProjectEventsExchange());
    }

    @Bean
    public TopicExchange projectMembershipEventsExchange() {
        return new TopicExchange(eventConstants.getProjectMembershipEventsExchange());
    }

    // Queues
    @Bean
    public Queue projectUserCreatedEventsQueue() {
        return new Queue(eventConstants.getProjectUserCreatedQueue(), true);
    }

    @Bean
    public Queue projectUserDeletedEventsQueue() {
        return new Queue(eventConstants.getProjectUserDeletedQueue(), true);
    }

    // Bindings
    @Bean
    public Binding bindingUserCreatedEvents() {
        return BindingBuilder.bind(projectUserCreatedEventsQueue())
                .to(userEventsExchange())
                .with(eventConstants.getUserCreatedRoutingKey());
    }

    @Bean
    public Binding bindingUserDeletedEvents() {
        return BindingBuilder.bind(projectUserDeletedEventsQueue())
                .to(userEventsExchange())
                .with(eventConstants.getUserDeletedRoutingKey());
    }
}