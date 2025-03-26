package com.taskify.auth.presentation.response;

import com.taskify.auth.old.entity.User;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RegisterResponse {
    private UUID id;
    private String username;
    private String email;

    public RegisterResponse(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public RegisterResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}