package com.taskify.organization.config;

import com.taskify.commoncore.event.EventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({com.taskify.commonweb.config.CommonRabbitMqConfig.class})
public class OrgRabbitMqConfig {

    private final EventConstants eventConstants;

    public OrgRabbitMqConfig(EventConstants eventConstants) {
        this.eventConstants = eventConstants;
    }

    // Exchanges
    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(eventConstants.getUserEventsExchange());
    }

    @Bean
    public TopicExchange membershipEventsExchange() {
        return new TopicExchange(eventConstants.getMembershipEventsExchange());
    }

    @Bean
    public TopicExchange organizationEventsExchange() {
        return new TopicExchange(eventConstants.getOrganizationEventsExchange());
    }

    // Queues
    @Bean
    public Queue orgUserCreatedEventsQueue() {
        return new Queue(eventConstants.getOrgUserCreatedQueue(), true);
    }

    @Bean
    public Queue orgUserDeletedEventsQueue() {
        return new Queue(eventConstants.getOrgUserDeletedQueue(), true);
    }

    // Bindings
    @Bean
    public Binding bindingUserCreatedEvents() {
        return BindingBuilder.bind(orgUserCreatedEventsQueue())
                .to(userEventsExchange())
                .with(eventConstants.getUserCreatedRoutingKey());
    }

    @Bean
    public Binding bindingUserDeletedEvents() {
        return BindingBuilder.bind(orgUserDeletedEventsQueue())
                .to(userEventsExchange())
                .with(eventConstants.getUserDeletedRoutingKey());
    }
}