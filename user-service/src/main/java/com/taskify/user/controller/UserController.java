package com.taskify.user.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taskify.user.dto.common.BaseCollectionResponse;
import com.taskify.user.dto.user.*;
import com.taskify.user.entity.User;
import com.taskify.user.mapper.UserMapper;
import com.taskify.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public ResponseEntity<BaseCollectionResponse<UserBasicDto>> findAll(
            @ModelAttribute UserCollectionRequest filter
    ) {
        Page<User> users = _userService.getAllUsers(filter);
        Page<UserBasicDto> userBasicDtos = users.map(_userMapper::toBasicDto);

        return ResponseEntity.ok(BaseCollectionResponse.from(userBasicDtos));
    }

    @GetMapping("/batch")
    public ResponseEntity<List<UserBasicDto>> getUsersByIds(@RequestBody @JsonProperty("ids") List<UUID> ids) {
        List<User> users = _userService.getUsersByIds(ids);

        return ResponseEntity.ok(_userMapper.toBasicDtoList(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id) {
        User user = _userService.getUserById(id);

        return ResponseEntity.ok(_userMapper.toDto(user));
    }

    @PostMapping()
    public ResponseEntity<UserBasicDto> createUser(@RequestBody CreateUserDto createUserDto) {
        User user = _userService.createUser(createUserDto);

        return ResponseEntity.ok(_userMapper.toBasicDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserBasicDto> updateUser(@PathVariable("id") UUID id, @RequestBody UpdateUserDto updateUserDto) {
        User user = _userService.updateUserById(id, updateUserDto);

        return ResponseEntity.ok(_userMapper.toBasicDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") UUID id) {
        User user = _userService.deleteUserById(id);

        return ResponseEntity.ok("User with id " + user.getId() + " deleted");
    }


    // For testing purposes
    @PostMapping("/admin")
    public ResponseEntity<String> createAdmin() {
        _userService.createAdmin();

        return ResponseEntity.ok("Admin created");
    }
}