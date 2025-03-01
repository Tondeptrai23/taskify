package com.taskify.auth.service;

import com.taskify.auth.entity.RefreshToken;
import com.taskify.common.error.UnauthorizedException;
import com.taskify.auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository _refreshTokenRepository;
    private final RefreshTokenEncoder _encoder;

    @Value("${security.refresh-token.expiration}")
    private long _expiration;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               RefreshTokenEncoder encoder) {
        this._refreshTokenRepository = refreshTokenRepository;
        this._encoder = encoder;
    }

    public RefreshToken findTokenByBase64Token(String base64Token){
        String hashedToken = _encoder.hashString(base64Token);
        log.info("Finding token by base64 token: {}", hashedToken);
        return _refreshTokenRepository.findByToken(hashedToken);
    }

    @Transactional
    public String generateToken(UUID userId) {
        // Generate random bytes
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);

        // Convert to Base64 to send to user
        String base64Token = Base64.getEncoder().encodeToString(randomBytes);

        // Hash for storage
        String hashedToken = _encoder.hashString(base64Token);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(hashedToken);
        refreshToken.setUserId(userId);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(_expiration));

        _refreshTokenRepository.save(refreshToken);
        return base64Token;
    }

    @Transactional
    public boolean verifyToken(String hashedToken) {
        RefreshToken refreshToken = _refreshTokenRepository.findByToken(hashedToken);
        if (refreshToken == null) {
            throw new UnauthorizedException("Token not found");
        }

        if (refreshToken.isRevoked()) {
            // Token reuse detected
            _refreshTokenRepository.revokeAllTokensOfUser(refreshToken.getUserId());

            throw new UnauthorizedException("Token has been revoked");
        }

        if (refreshToken.isExpired()) {
            throw new UnauthorizedException("Token has expired");
        }

        return true;
    }

    @Transactional
    public void revokeToken(String hashedToken) {
        _refreshTokenRepository.revokeToken(hashedToken);
    }
}
