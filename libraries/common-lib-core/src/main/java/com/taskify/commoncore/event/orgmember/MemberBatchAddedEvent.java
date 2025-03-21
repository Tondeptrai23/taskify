package com.taskify.commoncore.event.orgmember;

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
public class MemberBatchAddedEvent {
    private UUID organizationId;
    private List<MemberData> members;
    private ZonedDateTime timestamp;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberData {
        private UUID id;
        private UUID userId;
        private UUID roleId;
        private boolean isAdmin;
    }
}