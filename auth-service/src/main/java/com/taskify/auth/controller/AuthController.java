package com.taskify.auth.controller;

import com.taskify.auth.dto.auth.LoginRequest;
import com.taskify.auth.dto.auth.LoginResponse;
import com.taskify.auth.dto.user.UserDto;
import com.taskify.auth.entity.User;
import com.taskify.auth.mapper.UserMapper;
import com.taskify.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService _authService;
    private final UserMapper _userMapper;

    @Autowired
    public AuthController(AuthService authService,
                          UserMapper userMapper) {
        this._authService = authService;
        this._userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest
    ){
        var response = _authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(response));
    }

    @PostMapping("/me")
    public ResponseEntity<UserDto> verify(
            @RequestHeader("Authorization") String token
    ){
        var response = _authService.verify(token);
        return ResponseEntity.ok(_userMapper.toDto(response));
    }
}
