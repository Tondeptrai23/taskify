package com.taskify.iam.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node("PermissionGroup")
@Data
public class PermissionGroup {
    @Id
    private Long id;

    private String name;
    private String description;

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    private Set<Permission> permissions;
}