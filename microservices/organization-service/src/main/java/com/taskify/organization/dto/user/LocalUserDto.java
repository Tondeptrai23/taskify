package com.taskify.organization.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocalUserDto {
    private UUID id;
    private String username;
    private String email;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}