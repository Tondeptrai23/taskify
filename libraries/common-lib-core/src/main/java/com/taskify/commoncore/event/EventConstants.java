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
    private final String projectEventsExchange;
    private final String projectMembershipEventsExchange;

    // Routing keys - User events
    private final String userCreatedRoutingKey;
    private final String userDeletedRoutingKey;

    // Routing keys - Organization events
    private final String membershipAddedRoutingKey;
    private final String membershipRemovedRoutingKey;
    private final String membershipRoleUpdatedRoutingKey;
    private final String organizationCreatedRoutingKey;
    private final String organizationUpdatedRoutingKey;
    private final String organizationDeletedRoutingKey;

    // Routing keys - Project events
    private final String projectCreatedRoutingKey;
    private final String projectUpdatedRoutingKey;
    private final String projectDeletedRoutingKey;

    // Routing keys - Project membership events
    private final String projectMemberAddedRoutingKey;
    private final String projectMemberRemovedRoutingKey;
    private final String projectMemberRoleUpdatedRoutingKey;

    // Queue names - IAM service
    private final String iamUserCreatedQueue;
    private final String iamUserDeletedQueue;
    private final String iamMembershipAddedQueue;
    private final String iamMembershipRemovedQueue;
    private final String iamMembershipRoleUpdatedQueue;
    private final String iamOrganizationCreatedQueue;
    private final String iamOrganizationUpdatedQueue;
    private final String iamOrganizationDeletedQueue;
    private final String iamProjectCreatedQueue;
    private final String iamProjectUpdatedQueue;
    private final String iamProjectDeletedQueue;
    private final String iamProjectMemberAddedQueue;
    private final String iamProjectMemberRemovedQueue;
    private final String iamProjectMemberRoleUpdatedQueue;

    // Queue names - Organization service
    private final String orgUserCreatedQueue;
    private final String orgUserDeletedQueue;

    // Queue names - Project service
    private final String projectUserCreatedQueue;
    private final String projectUserDeletedQueue;

    @Autowired
    public EventConstants(
            // Exchange names
            @Value("${rabbitmq.exchange.user-events}") String userEventsExchange,
            @Value("${rabbitmq.exchange.membership-events}") String membershipEventsExchange,
            @Value("${rabbitmq.exchange.organization-events}") String organizationEventsExchange,
            @Value("${rabbitmq.exchange.project-events}") String projectEventsExchange,
            @Value("${rabbitmq.exchange.project-membership-events}") String projectMembershipEventsExchange,

            // Routing keys - User events
            @Value("${rabbitmq.routing-key.user-created}") String userCreatedRoutingKey,
            @Value("${rabbitmq.routing-key.user-deleted}") String userDeletedRoutingKey,

            // Routing keys - Organization events
            @Value("${rabbitmq.routing-key.membership-added}") String membershipAddedRoutingKey,
            @Value("${rabbitmq.routing-key.membership-removed}") String membershipRemovedRoutingKey,
            @Value("${rabbitmq.routing-key.membership-role-updated}") String membershipRoleUpdatedRoutingKey,
            @Value("${rabbitmq.routing-key.organization-created}") String organizationCreatedRoutingKey,
            @Value("${rabbitmq.routing-key.organization-updated}") String organizationUpdatedRoutingKey,
            @Value("${rabbitmq.routing-key.organization-deleted}") String organizationDeletedRoutingKey,

            // Routing keys - Project events
            @Value("${rabbitmq.routing-key.project-created}") String projectCreatedRoutingKey,
            @Value("${rabbitmq.routing-key.project-updated}") String projectUpdatedRoutingKey,
            @Value("${rabbitmq.routing-key.project-deleted}") String projectDeletedRoutingKey,
            @Value("${rabbitmq.routing-key.project-member-added}") String projectMemberAddedRoutingKey,
            @Value("${rabbitmq.routing-key.project-member-removed}") String projectMemberRemovedRoutingKey,
            @Value("${rabbitmq.routing-key.project-member-role-updated}") String projectMemberRoleUpdatedRoutingKey,

            // IAM service queues
            @Value("${rabbitmq.queue.iam-user-created}") String iamUserCreatedQueue,
            @Value("${rabbitmq.queue.iam-user-deleted}") String iamUserDeletedQueue,
            @Value("${rabbitmq.queue.iam-membership-added}") String iamMembershipAddedQueue,
            @Value("${rabbitmq.queue.iam-membership-removed}") String iamMembershipRemovedQueue,
            @Value("${rabbitmq.queue.iam-membership-role-updated}") String iamMembershipRoleUpdatedQueue,
            @Value("${rabbitmq.queue.iam-organization-created}") String iamOrganizationCreatedQueue,
            @Value("${rabbitmq.queue.iam-organization-updated}") String iamOrganizationUpdatedQueue,
            @Value("${rabbitmq.queue.iam-organization-deleted}") String iamOrganizationDeletedQueue,
            @Value("${rabbitmq.queue.iam-project-created}") String iamProjectCreatedQueue,
            @Value("${rabbitmq.queue.iam-project-updated}") String iamProjectUpdatedQueue,
            @Value("${rabbitmq.queue.iam-project-deleted}") String iamProjectDeletedQueue,
            @Value("${rabbitmq.queue.iam-project-member-added}") String iamProjectMemberAddedQueue,
            @Value("${rabbitmq.queue.iam-project-member-removed}") String iamProjectMemberRemovedQueue,
            @Value("${rabbitmq.queue.iam-project-member-role-updated}") String iamProjectMemberRoleUpdatedQueue,

            // Organization service queues
            @Value("${rabbitmq.queue.org-user-created}") String orgUserCreatedQueue,
            @Value("${rabbitmq.queue.org-user-deleted}") String orgUserDeletedQueue,

            // Project service queues
            @Value("${rabbitmq.queue.project-user-created}") String projectUserCreatedQueue,
            @Value("${rabbitmq.queue.project-user-deleted}") String projectUserDeletedQueue
    ) {

        this.userEventsExchange = userEventsExchange;
        this.membershipEventsExchange = membershipEventsExchange;
        this.organizationEventsExchange = organizationEventsExchange;
        this.projectEventsExchange = projectEventsExchange;
        this.projectMembershipEventsExchange = projectMembershipEventsExchange;

        this.userCreatedRoutingKey = userCreatedRoutingKey;
        this.userDeletedRoutingKey = userDeletedRoutingKey;
        this.membershipAddedRoutingKey = membershipAddedRoutingKey;
        this.membershipRemovedRoutingKey = membershipRemovedRoutingKey;
        this.membershipRoleUpdatedRoutingKey = membershipRoleUpdatedRoutingKey;
        this.organizationCreatedRoutingKey = organizationCreatedRoutingKey;
        this.organizationUpdatedRoutingKey = organizationUpdatedRoutingKey;
        this.organizationDeletedRoutingKey = organizationDeletedRoutingKey;
        this.projectCreatedRoutingKey = projectCreatedRoutingKey;
        this.projectUpdatedRoutingKey = projectUpdatedRoutingKey;
        this.projectDeletedRoutingKey = projectDeletedRoutingKey;
        this.projectMemberAddedRoutingKey = projectMemberAddedRoutingKey;
        this.projectMemberRemovedRoutingKey = projectMemberRemovedRoutingKey;
        this.projectMemberRoleUpdatedRoutingKey = projectMemberRoleUpdatedRoutingKey;

        this.iamUserCreatedQueue = iamUserCreatedQueue;
        this.iamUserDeletedQueue = iamUserDeletedQueue;
        this.iamMembershipAddedQueue = iamMembershipAddedQueue;
        this.iamMembershipRemovedQueue = iamMembershipRemovedQueue;
        this.iamMembershipRoleUpdatedQueue = iamMembershipRoleUpdatedQueue;
        this.iamOrganizationCreatedQueue = iamOrganizationCreatedQueue;
        this.iamOrganizationUpdatedQueue = iamOrganizationUpdatedQueue;
        this.iamOrganizationDeletedQueue = iamOrganizationDeletedQueue;
        this.iamProjectCreatedQueue = iamProjectCreatedQueue;
        this.iamProjectUpdatedQueue = iamProjectUpdatedQueue;
        this.iamProjectDeletedQueue = iamProjectDeletedQueue;
        this.iamProjectMemberAddedQueue = iamProjectMemberAddedQueue;
        this.iamProjectMemberRemovedQueue = iamProjectMemberRemovedQueue;
        this.iamProjectMemberRoleUpdatedQueue = iamProjectMemberRoleUpdatedQueue;

        this.orgUserCreatedQueue = orgUserCreatedQueue;
        this.orgUserDeletedQueue = orgUserDeletedQueue;

        this.projectUserCreatedQueue = projectUserCreatedQueue;
        this.projectUserDeletedQueue = projectUserDeletedQueue;
    }
}