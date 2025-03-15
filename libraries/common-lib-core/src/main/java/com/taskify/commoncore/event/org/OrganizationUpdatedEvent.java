package com.taskify.commoncore.event.org;

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
public class OrganizationUpdatedEvent {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private ZonedDateTime timestamp;
}