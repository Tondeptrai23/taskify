package com.taskify.organization.dto.membership;

import com.taskify.organization.dto.common.BaseCollectionRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class MembershipCollectionRequest extends BaseCollectionRequest {
    private String username;
    private String email;
    private UUID roleId;
    private Boolean isAdmin;
    private Boolean isActive;
    private ZonedDateTime joinedFrom;
    private ZonedDateTime joinedTo;
    private String sortBy = "joinedAt";
}
