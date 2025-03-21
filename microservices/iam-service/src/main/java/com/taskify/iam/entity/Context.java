package com.taskify.iam.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

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
    private String path;        // Hierarchical path for quick access

    @Relationship(type = "PARENT_OF", direction = Relationship.Direction.OUTGOING)
    private Set<Context> children = new HashSet<>();

    @Relationship(type = "CHILD_OF", direction = Relationship.Direction.OUTGOING)
    private Context parent;
}