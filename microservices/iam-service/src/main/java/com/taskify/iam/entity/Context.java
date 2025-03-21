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

@Node("Context")
@Data
public class Context {
    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;

    private String name;
    private ContextType type;
    private String externalId;  // Original entity ID (org ID or project ID)
    private String path;        // Hierarchical path for quick access

    @CreatedDate
    private ZonedDateTime createdAt;

    @LastModifiedDate
    private ZonedDateTime updatedAt;

    @Relationship(type = "PARENT_OF", direction = Relationship.Direction.OUTGOING)
    private Set<Context> children = new HashSet<>();

    @Relationship(type = "CHILD_OF", direction = Relationship.Direction.OUTGOING)
    private Context parent;
}