package com.taskify.iam.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;
import java.util.UUID;

@Node("User")
@Data
public class User {
    @Id
    private UUID id;

    private String username;
    private String email;
    private String systemRole;
    private boolean isDeleted;

    @Relationship(type = "HAS_ORG_ROLE", direction = Relationship.Direction.OUTGOING)
    private Set<OrganizationRole> organizationRoles;

    @Relationship(type = "HAS_PROJECT_ROLE", direction = Relationship.Direction.OUTGOING)
    private Set<ProjectRole> projectRoles;
}