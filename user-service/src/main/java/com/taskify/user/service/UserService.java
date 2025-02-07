package com.taskify.user.service;

import com.taskify.user.dto.CreateUserDto;
import com.taskify.user.dto.UpdateUserDto;
import com.taskify.user.entity.SystemRole;
import com.taskify.user.entity.User;
import com.taskify.user.mapper.UserMapper;
import com.taskify.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<User> getAllUsers() {
        return _userRepository.findAll();
    }

    public User createUser(CreateUserDto createUserDto) {
        User user = _userMapper.toEntity(createUserDto);
        user.setPasswordHash(createUserDto.getPassword());
        return _userRepository.save(user);
    }

    public User getUserById(String id) {
        return _userRepository.findById(UUID.fromString(id)).orElse(null);
    }

    public User updateUserById(String id, UpdateUserDto updateUserDto) {
        User user = this.getUserById(id);
        if (user == null) {
            return null;
        }

        if (updateUserDto.getUsername() != null) {
            user.setEmail(updateUserDto.getUsername());
        }

        if (updateUserDto.getPassword() != null) {
            user.setPasswordHash(updateUserDto.getPassword());
        }
        return _userRepository.save(user);
    }

    public User deleteUserById(String id) {
        User user = this.getUserById(id);
        if (user == null) {
            return null;
        }

        _userRepository.deleteById(UUID.fromString(id));
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