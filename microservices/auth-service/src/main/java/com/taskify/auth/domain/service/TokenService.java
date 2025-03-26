package com.taskify.auth.domain.service;

import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.entity.RefreshToken;
import java.util.UUID;

public interface TokenService {
    String generateAccessToken(User user);
    RefreshToken generateRefreshToken(UUID userId);
    String encodeRefreshTokenForTransmission(String rawToken);
    String decodeRefreshTokenFromTransmission(String encodedToken);
    boolean validateAccessToken(String token);
    UUID getUserIdFromAccessToken(String token);
}
