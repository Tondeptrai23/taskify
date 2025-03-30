package com.taskify.auth.domain.repository;

import com.taskify.auth.domain.entity.RefreshToken;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void revokeToken(String token);
    void revokeAllTokensForUser(UUID userId);
}
