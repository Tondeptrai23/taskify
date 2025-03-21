package com.taskify.project.dto.membership;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Setter
@Getter
public class ProjectMembershipDto {
    private UUID id;
    private UUID userId;
    private String username;
    private String email;
    private UUID roleId;
    private boolean isActive;
    private ZonedDateTime joinedAt;
}