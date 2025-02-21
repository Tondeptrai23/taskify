package com.taskify.auth.service;

import com.taskify.auth.entity.User;
import com.taskify.auth.exception.UnauthorizedException;
import com.taskify.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService _userService;
    private final UserRepository _userRepository;
    private final JwtService _jwtService;

    @Autowired
    public AuthService(UserService userService,
                       JwtService jwtService,
                       UserRepository userRepository) {
        this._userService = userService;
        this._jwtService = jwtService;
        this._userRepository = userRepository;
    }

    public Pair<User, String> login(String username, String password) {
        User user = _userRepository.findUserByUsername(username);
        if (user == null || !user.getPasswordHash().equals(password)) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = _jwtService.generateToken(user);
        return Pair.of(user, token);
    }
}
