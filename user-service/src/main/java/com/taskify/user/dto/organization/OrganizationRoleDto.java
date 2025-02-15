package com.taskify.user.dto.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrganizationRoleDto {
    private UUID id;
    private String name;
    private String description;
    private boolean isDefault;
}
