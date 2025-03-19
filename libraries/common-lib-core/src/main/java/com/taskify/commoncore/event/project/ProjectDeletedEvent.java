package com.taskify.commoncore.event.project;

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
public class ProjectDeletedEvent {
    private UUID id;
    private UUID organizationId;
    private ZonedDateTime timestamp;
}