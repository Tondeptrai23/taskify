package com.taskify.auth.service;

import com.taskify.auth.dto.user.CreateUserDto;
import com.taskify.auth.dto.user.UpdateUserDto;
import com.taskify.auth.dto.user.UserCollectionRequest;
import com.taskify.auth.entity.User;
import com.taskify.auth.event.UserEventPublisher;
import com.taskify.auth.mapper.UserMapper;
import com.taskify.auth.repository.UserRepository;
import com.taskify.auth.specification.UserSpecifications;
import com.taskify.commoncore.constant.SystemRole;
import com.taskify.commoncore.error.resource.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final UserRepository _userRepository;
    private final UserMapper _userMapper;
    private final PasswordEncoder _passwordEncoder;
    private final UserEventPublisher _userEventPublisher;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                          UserEventPublisher userEventPublisher,
                       PasswordEncoder passwordEncoder) {
        this._userRepository = userRepository;
        this._userMapper = userMapper;
        this._userEventPublisher = userEventPublisher;
        this._passwordEncoder = passwordEncoder;
    }

    public Page<User> getAllUsers(UserCollectionRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(
                        filter.getSortDirection().equalsIgnoreCase("asc")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        filter.getSortBy()
                )
        );

        return _userRepository.findAll(
                UserSpecifications.withFilters(filter),
                pageable
        );
    }

    public List<User> getUsersByIds(List<UUID> ids) {
        return _userRepository.findAllById(ids);
    }

    @Transactional
    public User createUser(CreateUserDto createUserDto) {
        User user = _userMapper.toEntity(createUserDto);
        user.setPasswordHash(_passwordEncoder.encode(createUserDto.getPassword()));

        var savedUser = _userRepository.save(user);

        _userEventPublisher.publishUserCreatedEvent(savedUser);

        return savedUser;
    }

    public User getUserById(UUID id) {
        return _userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public User updateUserById(UUID id, UpdateUserDto updateUserDto) {
        User user = this.getUserById(id);

        if (updateUserDto.getUsername() != null) {
            user.setEmail(updateUserDto.getUsername());
        }

        if (updateUserDto.getPassword() != null) {
            user.setPasswordHash(_passwordEncoder.encode(updateUserDto.getPassword()));
        }
        return _userRepository.save(user);
    }

    @Transactional
    public User deleteUserById(UUID id) {
        User user = this.getUserById(id);

        _userRepository.deleteById(id);

        _userEventPublisher.publishUserDeletedEvent(user);

        return user;
    }

    public boolean existsByEmail(String email) {
        return _userRepository.existsByEmail(email);
    }

    public User createAdmin() {
        if (this.existsByEmail("admin@taskify.com")) {
            return null;
        }
        User user = new User();
        user.setEmail("admin@taskify.com");
        user.setUsername("admin");
        user.setPasswordHash("admin");
        user.setSystemRole(SystemRole.SYSTEM_ADMIN);
        return _userRepository.save(user);
    }
}