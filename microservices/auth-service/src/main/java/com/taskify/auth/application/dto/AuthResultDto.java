package com.taskify.auth.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class AuthResultDto {
    private UUID userId;
    private String email;
    private String username;
    private String role;
    private String accessToken;
    private String refreshToken;
}
