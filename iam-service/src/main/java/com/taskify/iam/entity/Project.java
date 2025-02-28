package com.taskify.iam.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Node("Project")
@Data
public class Project {
    @Id
    private UUID id;

    private String name;
    private String description;
    private UUID organizationId;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private LocalOrganization organization;

    @Relationship(type = "HAS_ROLE", direction = Relationship.Direction.OUTGOING)
    private Set<ProjectRole> roles = new HashSet<>();
}