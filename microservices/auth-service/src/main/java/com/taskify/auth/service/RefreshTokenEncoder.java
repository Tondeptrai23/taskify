package com.taskify.auth.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenEncoder {
    /**
     * Hashes a string using SHA-256 algorithm
     */
    public String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                    input.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing string", e);
        }
    }
}
