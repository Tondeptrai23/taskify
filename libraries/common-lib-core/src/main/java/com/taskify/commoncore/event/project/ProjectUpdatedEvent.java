package com.taskify.commoncore.event.project;

import com.taskify.commoncore.constant.ProjectStatus;
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
public class ProjectUpdatedEvent {
    private UUID id;
    private String name;
    private String description;
    private String key;
    private UUID authorId;
    private ProjectStatus status;
    private ZonedDateTime timestamp;
}