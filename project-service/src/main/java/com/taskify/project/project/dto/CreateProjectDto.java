package com.taskify.project.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateProjectDto {
    private String name;
    private String description;
    private UUID organizationId;
}
