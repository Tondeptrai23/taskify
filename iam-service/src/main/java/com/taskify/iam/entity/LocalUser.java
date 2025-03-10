package com.taskify.iam.entity;

import com.taskify.common.constant.SystemRole;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;
import java.util.UUID;

@Node("User")
@Data
public class LocalUser {
    @Id
    private UUID id;

    private String username;
    private String email;

    private SystemRole systemRole;
    private boolean isDeleted = false;

    @Relationship(type = "HAS_ORG_ROLE", direction = Relationship.Direction.OUTGOING)
    private Set<Role> organizationRoles;

    @Relationship(type = "HAS_PROJECT_ROLE", direction = Relationship.Direction.OUTGOING)
    private Set<Role> projectRoles;
}