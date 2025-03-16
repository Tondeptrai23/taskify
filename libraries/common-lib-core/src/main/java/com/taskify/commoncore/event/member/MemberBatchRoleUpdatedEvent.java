package com.taskify.commoncore.event.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberBatchRoleUpdatedEvent {
    private UUID organizationId;
    private UUID newRoleId;
    private List<MemberRoleUpdate> members;
    private ZonedDateTime timestamp;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberRoleUpdate {
        private UUID id;
        private UUID userId;
        private UUID oldRoleId;
        private boolean isAdmin;
    }
}