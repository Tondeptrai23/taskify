package com.taskify.iam.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Node("Role")
@Data
public class Role {
    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;

    private String name;
    private String description;
    private boolean isDefault;

    // Role type to distinguish between organization and project roles
    private RoleType roleType;

    @CreatedDate
    private ZonedDateTime createdAt;

    @LastModifiedDate
    private ZonedDateTime updatedAt;

    @Relationship(type = "HAS_PERMISSION", direction = Relationship.Direction.OUTGOING)
    private Set<Permission> permissions = new HashSet<>();

    // Organization context (for organization roles)
    @Relationship(type = "BELONGS_TO_ORG", direction = Relationship.Direction.OUTGOING)
    private LocalOrganization organization;

    // Project context (for project roles)
    @Relationship(type = "BELONGS_TO_PROJECT", direction = Relationship.Direction.OUTGOING)
    private Project project;

    // Helper methods
    public UUID getContextId() {
        return RoleType.ORGANIZATION.equals(roleType) ?
                (organization != null ? organization.getId() : null) :
                (project != null ? project.getId() : null);
    }
}