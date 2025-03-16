package com.taskify.iam.config;

import com.taskify.commoncore.event.EventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({com.taskify.commonweb.config.CommonRabbitMqConfig.class})
public class IamRabbitMqConfig {

    private final EventConstants eventConstants;

    public IamRabbitMqConfig(EventConstants eventConstants) {
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

    // Queues (without dead letter configuration)
    @Bean
    public Queue iamUserCreatedEventsQueue() {
        return new Queue(eventConstants.getIamUserCreatedQueue(), true);
    }

    @Bean
    public Queue iamUserDeletedEventsQueue() {
        return new Queue(eventConstants.getIamUserDeletedQueue(), true);
    }

    @Bean
    public Queue iamMembershipAddedEventsQueue() {
        return new Queue(eventConstants.getIamMembershipAddedQueue(), true);
    }

    @Bean
    public Queue iamMembershipRemovedEventsQueue() {
        return new Queue(eventConstants.getIamMembershipRemovedQueue(), true);
    }

    @Bean
    public Queue iamMembershipRoleUpdatedEventsQueue() {
        return new Queue(eventConstants.getIamMembershipRoleUpdatedQueue(), true);
    }

    @Bean
    public Queue iamOrganizationCreatedEventsQueue() {
        return new Queue(eventConstants.getIamOrganizationCreatedQueue(), true);
    }

    @Bean
    public Queue iamOrganizationUpdatedEventsQueue() {
        return new Queue(eventConstants.getIamOrganizationUpdatedQueue(), true);
    }

    @Bean
    public Queue iamOrganizationDeletedEventsQueue() {
        return new Queue(eventConstants.getIamOrganizationDeletedQueue(), true);
    }

    // Bindings
    @Bean
    public Binding bindingUserCreatedEvents() {
        return BindingBuilder.bind(iamUserCreatedEventsQueue())
                .to(userEventsExchange())
                .with(eventConstants.getUserCreatedRoutingKey());
    }

    @Bean
    public Binding bindingUserDeletedEvents() {
        return BindingBuilder.bind(iamUserDeletedEventsQueue())
                .to(userEventsExchange())
                .with(eventConstants.getUserDeletedRoutingKey());
    }

    @Bean
    public Binding bindingMembershipAddedEvents() {
        return BindingBuilder.bind(iamMembershipAddedEventsQueue())
                .to(membershipEventsExchange())
                .with(eventConstants.getMembershipAddedRoutingKey());
    }

    @Bean
    public Binding bindingMembershipRemovedEvents() {
        return BindingBuilder.bind(iamMembershipRemovedEventsQueue())
                .to(membershipEventsExchange())
                .with(eventConstants.getMembershipRemovedRoutingKey());
    }

    @Bean
    public Binding bindingMembershipRoleUpdatedEvents() {
        return BindingBuilder.bind(iamMembershipRoleUpdatedEventsQueue())
                .to(membershipEventsExchange())
                .with(eventConstants.getMembershipRoleUpdatedRoutingKey());
    }

    @Bean
    public Binding bindingOrganizationCreatedEvents() {
        return BindingBuilder.bind(iamOrganizationCreatedEventsQueue())
                .to(organizationEventsExchange())
                .with(eventConstants.getOrganizationCreatedRoutingKey());
    }

    @Bean
    public Binding bindingOrganizationUpdatedEvents() {
        return BindingBuilder.bind(iamOrganizationUpdatedEventsQueue())
                .to(organizationEventsExchange())
                .with(eventConstants.getOrganizationUpdatedRoutingKey());
    }

    @Bean
    public Binding bindingOrganizationDeletedEvents() {
        return BindingBuilder.bind(iamOrganizationDeletedEventsQueue())
                .to(organizationEventsExchange())
                .with(eventConstants.getOrganizationDeletedRoutingKey());
    }
}