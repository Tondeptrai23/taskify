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
    private UUID contextId;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<String> permissions;
}
