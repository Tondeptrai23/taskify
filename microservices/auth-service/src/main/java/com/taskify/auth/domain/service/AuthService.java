package com.taskify.auth.domain.service;

import com.taskify.auth.domain.entity.RefreshToken;
import com.taskify.auth.domain.entity.User;

public interface AuthService {
    User authenticate(String username, String password);
    User validateToken(String token);
    RefreshToken refreshToken(String token);
    void revokeToken(String token);
    void revokeAllUserTokens(User user);
}
