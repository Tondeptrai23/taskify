package com.taskify.auth.application.contracts;

import com.taskify.auth.application.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserApplicationService {
    UserDto getUserById(UUID id);

    List<UserDto> getUsersByIds(List<UUID> ids);

    UserDto updateUser(UUID id, String username, String password);

    void deleteUser(UUID id);
}