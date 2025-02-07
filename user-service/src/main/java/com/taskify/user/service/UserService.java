package com.taskify.user.service;

import com.taskify.user.dto.CreateUserDto;
import com.taskify.user.dto.UpdateUserDto;
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
    public UserService(UserRepository userRepository,UserMapper userMapper) {
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

        user.setUsername(updateUserDto.getUsername());
        user.setPasswordHash(updateUserDto.getPassword());
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
}