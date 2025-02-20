package com.taskify.auth.service;

import com.taskify.auth.dto.user.CreateUserDto;
import com.taskify.auth.dto.user.UpdateUserDto;
import com.taskify.auth.dto.user.UserCollectionRequest;
import com.taskify.auth.entity.SystemRole;
import com.taskify.auth.entity.User;
import com.taskify.auth.exception.UserNotFoundException;
import com.taskify.auth.mapper.UserMapper;
import com.taskify.auth.repository.UserRepository;
import com.taskify.auth.specification.UserSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository _userRepository;
    private final UserMapper _userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this._userRepository = userRepository;
        this._userMapper = userMapper;
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
        user.setPasswordHash(createUserDto.getPassword());
        return _userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return _userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User updateUserById(UUID id, UpdateUserDto updateUserDto) {
        User user = this.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        if (updateUserDto.getUsername() != null) {
            user.setEmail(updateUserDto.getUsername());
        }

        if (updateUserDto.getPassword() != null) {
            user.setPasswordHash(updateUserDto.getPassword());
        }
        return _userRepository.save(user);
    }

    @Transactional
    public User deleteUserById(UUID id) {
        User user = this.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        _userRepository.deleteById(id);
        return user;
    }

    public boolean existsByEmail(String email) {
        User user = _userRepository.findUserByEmail(email);

        return user != null;
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