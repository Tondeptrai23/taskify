package com.taskify.auth.presentation.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LoginResponse {
    private UUID id;
    private String accessToken;
    private String refreshToken;
    private String email;
    private String username;
    private String role;
}
