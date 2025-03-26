package com.taskify.auth.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String role;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
