package com.taskify.iam.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Set;
import java.util.UUID;

@Node("Organization")
@Data
public class LocalOrganization {
    @Id
    private UUID id;

    private String name;
    private UUID ownerId;
}
