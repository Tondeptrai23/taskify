package com.taskify.auth.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.commoncore.dto.ApiCollectionResponse;
import com.taskify.auth.dto.user.*;
import com.taskify.auth.entity.User;
import com.taskify.auth.mapper.UserMapper;
import com.taskify.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService _userService;
    private final UserMapper _userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this._userService = userService;
        this._userMapper = userMapper;
    }

    @GetMapping({"/", ""})
    public ResponseEntity<ApiResponse<ApiCollectionResponse<UserBasicDto>>> findAll(
            @ModelAttribute UserCollectionRequest filter
    ) {
        Page<User> users = _userService.getAllUsers(filter);
        Page<UserBasicDto> userBasicDtos = users.map(_userMapper::toBasicDto);

        var response = new ApiResponse<>(ApiCollectionResponse.from(userBasicDtos));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/batch")
    public ResponseEntity<ApiResponse<List<UserBasicDto>>> getUsersByIds(@RequestBody @JsonProperty("ids") List<UUID> ids) {
        List<User> users = _userService.getUsersByIds(ids);
        var response = new ApiResponse<>(_userMapper.toBasicDtoList(users));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable("id") UUID id) {
        User user = _userService.getUserById(id);
        var response = new ApiResponse<>(_userMapper.toDto(user));
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/", ""})
    public ResponseEntity<ApiResponse<UserBasicDto>> createUser(@RequestBody CreateUserDto createUserDto) {
        User user = _userService.createUser(createUserDto);
        var response = new ApiResponse<>(_userMapper.toBasicDto(user));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserBasicDto>> updateUser(@PathVariable("id") UUID id, @RequestBody UpdateUserDto updateUserDto) {
        User user = _userService.updateUserById(id, updateUserDto);
        var response = new ApiResponse<>(_userMapper.toBasicDto(user));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable("id") UUID id) {
        User user = _userService.deleteUserById(id);
        var response = new ApiResponse<>("User with id " + user.getId() + " deleted");
        return ResponseEntity.ok(response);
    }

    // For testing purposes
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<String>> createAdmin() {
        _userService.createAdmin();
        var response = new ApiResponse<>("Admin created");
        return ResponseEntity.ok(response);
    }
}