package com.taskify.iam.dto.role;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoleDto {
    private UUID id;
    private String name;
    private String description;
    private boolean isDefault;
    private UUID organizationId;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<String> permissions; // This is a list of permissions that the role has
}
