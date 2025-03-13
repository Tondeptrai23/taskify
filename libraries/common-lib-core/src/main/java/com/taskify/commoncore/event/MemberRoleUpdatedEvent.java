package com.taskify.commoncore.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRoleUpdatedEvent {
    private UUID id;
    private UUID organizationId;
    private UUID userId;
    private UUID newRoleId;
    private UUID oldRoleId;
    private boolean isAdmin;
    private ZonedDateTime timestamp;
}