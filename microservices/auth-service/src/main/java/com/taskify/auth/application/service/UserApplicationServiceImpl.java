package com.taskify.auth.application.service;

import com.taskify.auth.application.contracts.UserApplicationService;
import com.taskify.auth.application.dto.UserDto;
import com.taskify.auth.application.mapper.UserMapper;
import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.exception.InvalidCredentialsException;
import com.taskify.auth.domain.repository.UserRepository;
import com.taskify.auth.domain.service.PasswordEncoder;

import java.util.List;
import java.util.UUID;

public class UserApplicationServiceImpl implements UserApplicationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserApplicationServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<UUID> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDtoList(users);
    }

    @Override
    public UserDto updateUser(UUID id, String username, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (username != null && !username.isBlank()) {
            user.setUsername(username);
        }

        if (password != null && !password.isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        userRepository.deleteById(id);
    }
}