package com.taskify.auth.dto.auth;

import com.taskify.auth.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;

@Getter
@Setter
public class LoginResponse {
    private String id;
    private String token;
    private String email;
    private String role;

    public LoginResponse(String id, String token, String email, String role) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public LoginResponse(Pair<User, String> response) {
        this.id = response.getFirst().getId().toString();
        this.token = response.getSecond();
        this.email = response.getFirst().getEmail();
        this.role = response.getFirst().getSystemRole().toString();
    }
}
