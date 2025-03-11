package com.taskify.commoncore.event;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedEvent {
    private UUID id;
    private String username;
    private String email;
    private String systemRole;
    private ZonedDateTime createdAt;
}