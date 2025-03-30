package com.taskify.auth.presentation.response;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String role;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}