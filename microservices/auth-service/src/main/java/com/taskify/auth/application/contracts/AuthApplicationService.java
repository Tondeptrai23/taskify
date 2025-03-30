package com.taskify.auth.application.contracts;

import com.taskify.auth.application.dto.AuthResultDto;
import com.taskify.auth.application.dto.LoginDto;
import com.taskify.auth.application.dto.RegisterUserDto;
import com.taskify.auth.application.dto.UserDto;

public interface AuthApplicationService {
    AuthResultDto login(LoginDto loginDto);
    UserDto register(RegisterUserDto registerDto);
    AuthResultDto refreshToken(String refreshToken);
    UserDto verifyToken(String token);
    void logout(String refreshToken);
}
