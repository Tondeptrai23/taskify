package com.taskify.iam.dto.permission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyPermissionRequest {
    private String permission;
    private String projectId;
}
