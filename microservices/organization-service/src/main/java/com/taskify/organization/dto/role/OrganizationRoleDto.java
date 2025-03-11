package com.taskify.organization.dto.role;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrganizationRoleDto {
    private UUID id;
    private String name;
    private String description;
    private boolean isDefault;
    private UUID organizationId;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<String> permissions;
}
