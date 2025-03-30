package com.taskify.auth.application.service;

import com.taskify.auth.application.contracts.UserApplicationService;
import com.taskify.auth.application.contracts.UserEventPublisher;
import com.taskify.auth.application.dto.UserDto;
import com.taskify.auth.application.exception.AuthApplicationException;
import com.taskify.auth.application.exception.AuthErrorCode;
import com.taskify.auth.application.mapper.UserMapper;
import com.taskify.auth.domain.entity.User;
import com.taskify.auth.domain.repository.UserRepository;
import com.taskify.auth.domain.contracts.PasswordEncoder;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserApplicationServiceImpl implements UserApplicationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;

    public UserApplicationServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            UserEventPublisher userEventPublisher
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userEventPublisher = userEventPublisher;
    }

    @Override
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthApplicationException("User not found", AuthErrorCode.USER_NOT_FOUND));

        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<UUID> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDtoList(users);
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, String username, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthApplicationException("User not found", AuthErrorCode.USER_NOT_FOUND));

        if (username != null && !username.isBlank()) {
            user.setUsername(username);
        }

        user.savePassword(password, passwordEncoder);

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthApplicationException("User not found", AuthErrorCode.USER_NOT_FOUND));

        user.markDeleted();
        userRepository.deleteById(id);

        userEventPublisher.publishUserDeletedEvent(user);
    }
}