package com.taskify.auth.infrastructure.service;

import com.taskify.auth.domain.entity.RefreshToken;
import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.exception.InvalidCredentialsException;
import com.taskify.auth.domain.exception.TokenValidationException;
import com.taskify.auth.domain.repository.RefreshTokenRepository;
import com.taskify.auth.domain.repository.UserRepository;
import com.taskify.auth.domain.service.AuthService;
import com.taskify.auth.domain.service.PasswordEncoder;
import com.taskify.auth.domain.service.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
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

    @Override
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return user;
    }

    @Override
    public User validateToken(String token) {
        if (!tokenService.validateAccessToken(token)) {
            throw new TokenValidationException("Invalid token");
        }

        UUID userId = tokenService.getUserIdFromAccessToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new TokenValidationException("User not found"));
    }

    @Override
    @Transactional
    public RefreshToken refreshToken(String token) {
        RefreshToken existingToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenValidationException("Refresh token not found"));

        if (existingToken.isRevoked()) {
            // Token reuse detected, revoke all tokens for this user
            refreshTokenRepository.revokeAllTokensForUser(existingToken.getUserId());
            throw new TokenValidationException("Refresh token has been revoked");
        }

        if (existingToken.isExpired()) {
            throw new TokenValidationException("Refresh token has expired");
        }

        // Revoke the old token
        refreshTokenRepository.revokeToken(token);

        // Generate a new refresh token
        RefreshToken newToken = tokenService.generateRefreshToken(existingToken.getUserId());
        return refreshTokenRepository.save(newToken);
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.revokeToken(token);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllTokensForUser(user.getId());
    }
}