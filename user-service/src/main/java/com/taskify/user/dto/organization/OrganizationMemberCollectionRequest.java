package com.taskify.user.dto.organization;

import com.taskify.user.dto.common.BaseCollectionRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class OrganizationMemberCollectionRequest extends BaseCollectionRequest {
    private String username;
    private String email;
    private String roleId;
    private Boolean isAdmin;
    private Boolean isActive;
    private ZonedDateTime joinedFrom;
    private ZonedDateTime joinedTo;
    private String sortBy = "joinedAt";
}
