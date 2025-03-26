package com.taskify.auth.old.repository;

import com.taskify.auth.old.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    RefreshToken findByToken(String token);

    @Modifying
    @Query(
        value = "UPDATE refresh_tokens SET is_revoked = true WHERE token = ?1",
        nativeQuery = true
    )
    void revokeToken(String token);

    @Modifying
    @Query(
        value = "UPDATE refresh_tokens SET is_revoked = true WHERE user_id = ?1",
        nativeQuery = true
    )
    void revokeAllTokensOfUser(UUID userId);
}
