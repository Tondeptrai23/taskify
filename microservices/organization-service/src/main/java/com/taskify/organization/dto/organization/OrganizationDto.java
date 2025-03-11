package com.taskify.organization.dto.organization;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class OrganizationDto {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}