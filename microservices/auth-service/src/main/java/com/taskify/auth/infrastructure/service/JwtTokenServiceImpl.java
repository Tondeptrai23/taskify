package com.taskify.auth.infrastructure.service;

import com.taskify.auth.domain.entity.RefreshToken;
import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.contracts.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class JwtTokenServiceImpl implements TokenService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${security.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private String encodedSecret;

    @PostConstruct
    public void init() {
        this.encodedSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    @Override
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("roles", user.getSystemRole())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, encodedSecret)
                .compact();
    }

    @Override
    public RefreshToken generateRefreshToken(UUID userId) {
        // Generate random bytes for token
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);

        // Convert to hex string for raw token
        String rawTokenValue = HexFormat.of().formatHex(randomBytes);

        // Hash the token for storage
        String hashedToken = hashString(rawTokenValue);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(hashedToken);
        refreshToken.setUserId(userId);
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setRevoked(false);
        refreshToken.setRawToken(rawTokenValue);

        return refreshToken;
    }

    @Override
    public String encodeRefreshTokenForTransmission(String rawToken) {
        return Base64.getEncoder().encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decodeRefreshTokenFromTransmission(String encodedToken) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedToken);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    @Override
    public String hashTokenForStorage(String rawToken) {
        return hashString(rawToken);
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(encodedSecret).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public UUID getUserIdFromAccessToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(encodedSecret)
                .parseClaimsJws(token)
                .getBody();

        return UUID.fromString(claims.getSubject());
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                    input.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing string", e);
        }
    }
}