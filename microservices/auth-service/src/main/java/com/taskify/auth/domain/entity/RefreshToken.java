package com.taskify.auth.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class RefreshToken {
    private UUID id;
    private String token;
    private UUID userId;
    private boolean revoked;
    private Instant expiresAt;
    private Instant createdAt;

    // Domain behavior
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !isExpired() && !revoked;
    }
}