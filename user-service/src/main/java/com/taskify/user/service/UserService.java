package com.taskify.user.service;

import com.taskify.user.entity.User;
import com.taskify.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository _userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this._userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return _userRepository.findAll();
    }
}