// controller/UserController.java
package com.taskify.user.controller;

import com.taskify.user.dto.UserBasicDto;
import com.taskify.user.entity.User;
import com.taskify.user.mapper.UserMapper;
import com.taskify.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService _userService;
    private final UserMapper _userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this._userService = userService;
        this._userMapper = userMapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserBasicDto>> getAllUsers() {
        List<User> users = _userService.getAllUsers();
        
        return ResponseEntity.ok(_userMapper.toBasicDtoList(users));
    }
}