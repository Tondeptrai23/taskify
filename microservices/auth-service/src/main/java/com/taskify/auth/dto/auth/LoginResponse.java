package com.taskify.auth.dto.auth;

import com.taskify.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String id;
    private String accessToken;
    private String refreshToken;
    private String email;
    private String role;

    public LoginResponse(Pair<User, AuthTokens> response) {
        this.id = response.getFirst().getId().toString();
        this.email = response.getFirst().getEmail();
        this.role = response.getFirst().getSystemRole().toString();
        this.accessToken = response.getSecond().getAccessToken();
        this.refreshToken = response.getSecond().getRefreshToken();
    }
}
