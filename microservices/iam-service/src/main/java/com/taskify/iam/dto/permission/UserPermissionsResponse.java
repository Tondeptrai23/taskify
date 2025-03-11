package com.taskify.iam.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserPermissionsResponse {
    private UUID organizationId;
    private List<String> permissions;
}
