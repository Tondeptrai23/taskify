package com.taskify.project.project.dto;

import com.taskify.project.project.entity.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateProjectDto {
    private String name;
    private String description;
    private UUID organizationId;
    private ProjectStatus status;
}
