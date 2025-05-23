package com.taskify.auth.presentation.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthTokens {
    private String accessToken;
    private String refreshToken;

    public AuthTokens(String newAccessToken, String newRefreshToken) {
        this.accessToken = newAccessToken;
        this.refreshToken = newRefreshToken;
    }
}
