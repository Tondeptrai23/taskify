package com.taskify.auth.presentation.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class RegisterResponse {
    private UUID id;
    private String username;
    private String email;
}