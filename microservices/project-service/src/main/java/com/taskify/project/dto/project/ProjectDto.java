package com.taskify.project.dto.project;

import com.taskify.project.entity.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class ProjectDto {
    private UUID id;
    private String name;
    private String description;
    private String key;
    private UUID organizationId;
    private UUID authorId;
    private ProjectStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}