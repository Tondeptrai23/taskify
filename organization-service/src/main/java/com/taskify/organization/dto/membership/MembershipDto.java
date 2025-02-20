package com.taskify.organization.dto.membership;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
public class MembershipDto {
    private String id;
    private String userId;
    private String username;
    private String email;
    private String roleId;
    private String roleName;
    private boolean isAdmin;
    private boolean isActive;
    private ZonedDateTime joinedAt;
}