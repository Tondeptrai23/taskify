package com.taskify.iam.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
    private UUID id;

    private String name;
    private String description;
    private boolean isDefault;
    private UUID organizationId;

    @CreatedDate
    private ZonedDateTime createdAt;

    @LastModifiedDate
    private ZonedDateTime updatedAt;

    @Relationship(type = "HAS_PERMISSION", direction = Relationship.Direction.OUTGOING)
    private Set<Permission> permissions = new HashSet<>();
}
