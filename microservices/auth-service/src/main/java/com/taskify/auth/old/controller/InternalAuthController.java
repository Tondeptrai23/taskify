package com.taskify.auth.old.controller;

import com.taskify.auth.old.dto.user.UserDto;
import com.taskify.auth.old.mapper.UserMapper;
import com.taskify.auth.old.service.AuthService;
import com.taskify.commoncore.annotation.LoggingBefore;
import com.taskify.commoncore.dto.ApiResponse;
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
public class InternalAuthController {
    private final AuthService _authService;
    private final UserMapper _userMapper;

    @Autowired
    public InternalAuthController(AuthService authService, UserMapper userMapper) {
        this._authService = authService;
        this._userMapper = userMapper;
    }

    @LoggingBefore
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<UserDto>> verify(
            @RequestHeader("Authorization") String token
    ){
        var response = _authService.verify(token);
        return ResponseEntity.ok(new ApiResponse<>(_userMapper.toDto(response)));
    }
}