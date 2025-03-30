package com.taskify.auth.domain.entity;

import com.taskify.auth.domain.contracts.PasswordEncoder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class User {
    private UUID id;
    private String email;
    private String username;
    private String passwordHash;
    private SystemRole systemRole;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void savePassword(String password, PasswordEncoder passwordEncoder) {
        if (password == null) {
            return;
        }

        if (password.isBlank()) {
            return;
        }

        this.passwordHash = passwordEncoder.encode(password);
    }

    public void markDeleted() {
        this.deletedAt = ZonedDateTime.now();
    }
}
