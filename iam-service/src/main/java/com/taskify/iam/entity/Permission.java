package com.taskify.iam.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Node("Permission")
@Data
public class Permission {
    @Id
    private Long id;

    private String name;
    private String description;
    private List<String> prerequisites;
}
