package com.taskify.commoncore.event;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EventConstants {

    // Exchange names
    private final String userEventsExchange;
    private final String membershipEventsExchange;
    private final String organizationEventsExchange;

    // Routing keys - direct definitions
    private final String userCreatedRoutingKey;
    private final String userDeletedRoutingKey;
    private final String membershipAddedRoutingKey;
    private final String membershipRemovedRoutingKey;
    private final String membershipRoleUpdatedRoutingKey;
    private final String organizationCreatedRoutingKey;
    private final String organizationUpdatedRoutingKey;
    private final String organizationDeletedRoutingKey;

    // Queue names - direct definitions for IAM service
    private final String iamUserCreatedQueue;
    private final String iamUserDeletedQueue;
    private final String iamMembershipAddedQueue;
    private final String iamMembershipRemovedQueue;
    private final String iamMembershipRoleUpdatedQueue;
    private final String iamOrganizationCreatedQueue;
    private final String iamOrganizationUpdatedQueue;
    private final String iamOrganizationDeletedQueue;

    // Queue names - direct definitions for Organization service
    private final String orgUserCreatedQueue;
    private final String orgUserDeletedQueue;

    // Queue names - direct definitions for Project service
    private final String projectUserCreatedQueue;
    private final String projectUserDeletedQueue;

    @Autowired
    public EventConstants(
            // Exchange names
            @Value("${rabbitmq.exchange.user-events}") String userEventsExchange,
            @Value("${rabbitmq.exchange.membership-events}") String membershipEventsExchange,
            @Value("${rabbitmq.exchange.organization-events}") String organizationEventsExchange,

            // Routing keys
            @Value("${rabbitmq.routing-key.user-created}") String userCreatedRoutingKey,
            @Value("${rabbitmq.routing-key.user-deleted}") String userDeletedRoutingKey,
            @Value("${rabbitmq.routing-key.membership-added}") String membershipAddedRoutingKey,
            @Value("${rabbitmq.routing-key.membership-removed}") String membershipRemovedRoutingKey,
            @Value("${rabbitmq.routing-key.membership-role-updated}") String membershipRoleUpdatedRoutingKey,
            @Value("${rabbitmq.routing-key.organization-created}") String organizationCreatedRoutingKey,
            @Value("${rabbitmq.routing-key.organization-updated}") String organizationUpdatedRoutingKey,
            @Value("${rabbitmq.routing-key.organization-deleted}") String organizationDeletedRoutingKey,

            // IAM service queues
            @Value("${rabbitmq.queue.iam-user-created}") String iamUserCreatedQueue,
            @Value("${rabbitmq.queue.iam-user-deleted}") String iamUserDeletedQueue,
            @Value("${rabbitmq.queue.iam-membership-added}") String iamMembershipAddedQueue,
            @Value("${rabbitmq.queue.iam-membership-removed}") String iamMembershipRemovedQueue,
            @Value("${rabbitmq.queue.iam-membership-role-updated}") String iamMembershipRoleUpdatedQueue,
            @Value("${rabbitmq.queue.iam-organization-created}") String iamOrganizationCreatedQueue,
            @Value("${rabbitmq.queue.iam-organization-updated}") String iamOrganizationUpdatedQueue,
            @Value("${rabbitmq.queue.iam-organization-deleted}") String iamOrganizationDeletedQueue,

            // Organization service queues
            @Value("${rabbitmq.queue.org-user-created}") String orgUserCreatedQueue,
            @Value("${rabbitmq.queue.org-user-deleted}") String orgUserDeletedQueue,

            // Project service queues
            @Value("${rabbitmq.queue.project-user-created}") String projectUserCreatedQueue,
            @Value("${rabbitmq.queue.project-user-deleted}") String projectUserDeletedQueue) {

        this.userEventsExchange = userEventsExchange;
        this.membershipEventsExchange = membershipEventsExchange;
        this.organizationEventsExchange = organizationEventsExchange;

        this.userCreatedRoutingKey = userCreatedRoutingKey;
        this.userDeletedRoutingKey = userDeletedRoutingKey;
        this.membershipAddedRoutingKey = membershipAddedRoutingKey;
        this.membershipRemovedRoutingKey = membershipRemovedRoutingKey;
        this.membershipRoleUpdatedRoutingKey = membershipRoleUpdatedRoutingKey;
        this.organizationCreatedRoutingKey = organizationCreatedRoutingKey;
        this.organizationUpdatedRoutingKey = organizationUpdatedRoutingKey;
        this.organizationDeletedRoutingKey = organizationDeletedRoutingKey;

        this.iamUserCreatedQueue = iamUserCreatedQueue;
        this.iamUserDeletedQueue = iamUserDeletedQueue;
        this.iamMembershipAddedQueue = iamMembershipAddedQueue;
        this.iamMembershipRemovedQueue = iamMembershipRemovedQueue;
        this.iamMembershipRoleUpdatedQueue = iamMembershipRoleUpdatedQueue;
        this.iamOrganizationCreatedQueue = iamOrganizationCreatedQueue;
        this.iamOrganizationUpdatedQueue = iamOrganizationUpdatedQueue;
        this.iamOrganizationDeletedQueue = iamOrganizationDeletedQueue;

        this.orgUserCreatedQueue = orgUserCreatedQueue;
        this.orgUserDeletedQueue = orgUserDeletedQueue;

        this.projectUserCreatedQueue = projectUserCreatedQueue;
        this.projectUserDeletedQueue = projectUserDeletedQueue;
    }
}