package com.taskify.auth.domain.service;

import com.taskify.auth.domain.contracts.PasswordEncoder;
import com.taskify.auth.domain.contracts.TokenService;
import com.taskify.auth.domain.entity.RefreshToken;
import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.exception.InvalidCredentialsException;
import com.taskify.auth.domain.exception.TokenExpiredException;
import com.taskify.auth.domain.exception.TokenRevokedException;
import com.taskify.auth.domain.exception.TokenValidationException;
import com.taskify.auth.domain.repository.RefreshTokenRepository;
import com.taskify.auth.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthDomainService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthDomainService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            TokenService tokenService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return user;
    }

    public User validateToken(String token) {
        if (!tokenService.validateAccessToken(token)) {
            throw new TokenValidationException("Invalid token");
        }

        UUID userId = tokenService.getUserIdFromAccessToken(token);
        return userRepository.findById(userId).orElse(null);
    }

    public RefreshToken refreshToken(String token) {
        RefreshToken existingToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenValidationException("Refresh token not found"));

        if (existingToken.isRevoked()) {
            refreshTokenRepository.revokeAllTokensForUser(existingToken.getUserId());
            throw new TokenRevokedException("Refresh token has been revoked");
        }

        if (existingToken.isExpired()) {
            throw new TokenExpiredException("Refresh token", LocalDateTime.now());
        }

        // Revoke the old token
        refreshTokenRepository.revokeToken(token);

        // Generate a new refresh token
        RefreshToken newToken = tokenService.generateRefreshToken(existingToken.getUserId());
        refreshTokenRepository.save(newToken);

        return newToken;
    }

    public void revokeToken(String token) {
        refreshTokenRepository.revokeToken(token);
    }

    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllTokensForUser(user.getId());
    }
}