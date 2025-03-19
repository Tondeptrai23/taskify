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

    @Bean
    public TopicExchange projectEventsExchange() {
        return new TopicExchange(eventConstants.getProjectEventsExchange());
    }

    @Bean
    public TopicExchange projectMembershipEventsExchange() {
        return new TopicExchange(eventConstants.getProjectMembershipEventsExchange());
    }

    // User Event Queues
    @Bean
    public Queue iamUserCreatedEventsQueue() {
        return new Queue(eventConstants.getIamUserCreatedQueue(), true);
    }

    @Bean
    public Queue iamUserDeletedEventsQueue() {
        return new Queue(eventConstants.getIamUserDeletedQueue(), true);
    }

    // Organization Event Queues
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

    // Membership Event Queues
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

    // Project Event Queues
    @Bean
    public Queue iamProjectCreatedEventsQueue() {
        return new Queue(eventConstants.getIamProjectCreatedQueue(), true);
    }

    @Bean
    public Queue iamProjectUpdatedEventsQueue() {
        return new Queue(eventConstants.getIamProjectUpdatedQueue(), true);
    }

    @Bean
    public Queue iamProjectDeletedEventsQueue() {
        return new Queue(eventConstants.getIamProjectDeletedQueue(), true);
    }

    // Project Membership Event Queues
    @Bean
    public Queue iamProjectMemberAddedEventsQueue() {
        return new Queue(eventConstants.getIamProjectMemberAddedQueue(), true);
    }

    @Bean
    public Queue iamProjectMemberRemovedEventsQueue() {
        return new Queue(eventConstants.getIamProjectMemberRemovedQueue(), true);
    }

    @Bean
    public Queue iamProjectMemberRoleUpdatedEventsQueue() {
        return new Queue(eventConstants.getIamProjectMemberRoleUpdatedQueue(), true);
    }

    // User Event Bindings
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

    // Organization Event Bindings
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

    // Membership Event Bindings
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

    // Project Event Bindings
    @Bean
    public Binding bindingProjectCreatedEvents() {
        return BindingBuilder.bind(iamProjectCreatedEventsQueue())
                .to(projectEventsExchange())
                .with(eventConstants.getProjectCreatedRoutingKey());
    }

    @Bean
    public Binding bindingProjectUpdatedEvents() {
        return BindingBuilder.bind(iamProjectUpdatedEventsQueue())
                .to(projectEventsExchange())
                .with(eventConstants.getProjectUpdatedRoutingKey());
    }

    @Bean
    public Binding bindingProjectDeletedEvents() {
        return BindingBuilder.bind(iamProjectDeletedEventsQueue())
                .to(projectEventsExchange())
                .with(eventConstants.getProjectDeletedRoutingKey());
    }

    // Project Membership Event Bindings
    @Bean
    public Binding bindingProjectMemberAddedEvents() {
        return BindingBuilder.bind(iamProjectMemberAddedEventsQueue())
                .to(projectMembershipEventsExchange())
                .with(eventConstants.getProjectMemberAddedRoutingKey());
    }

    @Bean
    public Binding bindingProjectMemberRemovedEvents() {
        return BindingBuilder.bind(iamProjectMemberRemovedEventsQueue())
                .to(projectMembershipEventsExchange())
                .with(eventConstants.getProjectMemberRemovedRoutingKey());
    }

    @Bean
    public Binding bindingProjectMemberRoleUpdatedEvents() {
        return BindingBuilder.bind(iamProjectMemberRoleUpdatedEventsQueue())
                .to(projectMembershipEventsExchange())
                .with(eventConstants.getProjectMemberRoleUpdatedRoutingKey());
    }
}