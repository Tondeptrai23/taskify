package com.taskify.auth.controller;

import com.taskify.auth.dto.user.UserDto;
import com.taskify.auth.mapper.UserMapper;
import com.taskify.auth.service.AuthService;
import com.taskify.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/internal")
public class InternalController {
    private final AuthService _authService;
    private final UserMapper _userMapper;

    @Autowired
    public InternalController(AuthService authService, UserMapper userMapper) {
        this._authService = authService;
        this._userMapper = userMapper;
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<UserDto>> verify(
            @RequestHeader("Authorization") String token
    ){
        log.info("Verifying token: {}", token);
        var response = _authService.verify(token);
        return ResponseEntity.ok(new ApiResponse<>(_userMapper.toDto(response)));
    }
}