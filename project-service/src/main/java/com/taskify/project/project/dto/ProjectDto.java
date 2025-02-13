package com.taskify.project.project.dto;

import com.taskify.project.project.entity.ProjectStatus;
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
    private UUID organizationId;
    private ProjectStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private ZonedDateTime deletedAt;
}
