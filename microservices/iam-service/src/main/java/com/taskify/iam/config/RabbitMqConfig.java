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

    @Value("${rabbitmq.exchange.membership-events}")
    private String membershipEventsExchange;

    @Value("${rabbitmq.queue.iam-user-created-events}")
    private String iamUserCreatedEventsQueue;

    @Value("${rabbitmq.queue.iam-user-deleted-events}")
    private String iamUserDeletedEventsQueue;

    @Value("${rabbitmq.queue.iam-membership-added-events}")
    private String iamMembershipAddedEventsQueue;

    @Value("${rabbitmq.queue.iam-membership-removed-events}")
    private String iamMembershipRemovedEventsQueue;

    @Value("${rabbitmq.queue.iam-membership-role-updated-events}")
    private String iamMembershipRoleUpdatedEventsQueue;

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(userEventsExchange);
    }

    @Bean
    public TopicExchange membershipEventsExchange() {
        return new TopicExchange(membershipEventsExchange);
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
    public Queue iamMembershipAddedEventsQueue() {
        return new Queue(iamMembershipAddedEventsQueue, true);
    }

    @Bean
    public Queue iamMembershipRemovedEventsQueue() {
        return new Queue(iamMembershipRemovedEventsQueue, true);
    }

    @Bean
    public Queue iamMembershipRoleUpdatedEventsQueue() {
        return new Queue(iamMembershipRoleUpdatedEventsQueue, true);
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
    public Binding bindingMembershipAddedEvents() {
        return BindingBuilder.bind(iamMembershipAddedEventsQueue())
                .to(membershipEventsExchange())
                .with("membership.added");
    }

    @Bean
    public Binding bindingMembershipRemovedEvents() {
        return BindingBuilder.bind(iamMembershipRemovedEventsQueue())
                .to(membershipEventsExchange())
                .with("membership.removed");
    }

    @Bean
    public Binding bindingMembershipRoleUpdatedEvents() {
        return BindingBuilder.bind(iamMembershipRoleUpdatedEventsQueue())
                .to(membershipEventsExchange())
                .with("membership.role.updated");
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