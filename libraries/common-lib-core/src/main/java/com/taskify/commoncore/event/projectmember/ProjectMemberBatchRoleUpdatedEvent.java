package com.taskify.commoncore.event.projectmember;

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
public class ProjectMemberBatchRoleUpdatedEvent {
    private UUID projectId;
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
    }
}