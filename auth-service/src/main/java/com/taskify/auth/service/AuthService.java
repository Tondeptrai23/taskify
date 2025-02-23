package com.taskify.auth.service;

import com.taskify.auth.dto.auth.AuthTokens;
import com.taskify.auth.entity.RefreshToken;
import com.taskify.auth.entity.User;
import com.taskify.auth.exception.UnauthorizedException;
import com.taskify.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final UserService _userService;
    private final UserRepository _userRepository;
    private final JwtService _jwtService;
    private final RefreshTokenService _refreshTokenService;

    @Autowired
    public AuthService(UserService userService,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       UserRepository userRepository) {
        this._userService = userService;
        this._jwtService = jwtService;
        this._refreshTokenService = refreshTokenService;
        this._userRepository = userRepository;
    }

    public Pair<User, AuthTokens> login(String username, String password) {
        User user = _userRepository.findUserByUsername(username);
        if (user == null || !user.getPasswordHash().equals(password)) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = _jwtService.generateToken(user);
        String refreshToken = _refreshTokenService.generateToken(user.getId());
        return Pair.of(user, new AuthTokens(token, refreshToken));
    }

    public User verify(String token) {
        var tokenParts = token.split(" ");

        var claims = _jwtService.getClaims(tokenParts[1]);

        User user = _userRepository.findUserById(UUID.fromString(claims.getSubject()));
        if (user == null) {
            throw new UnauthorizedException("Invalid token");
        }

        return user;
    }

    public AuthTokens refresh(String token) {
        var hashedToken = _refreshTokenService.findTokenByBase64Token(token);

        User user = _userRepository.findUserById(hashedToken.getUserId());
        if (user == null) {
            throw new UnauthorizedException("Invalid token");
        }

        _refreshTokenService.verifyToken(hashedToken.getToken());

        var newRefreshToken = _refreshTokenService.generateToken(hashedToken.getUserId());
        var newAccessToken = _jwtService.generateToken(user);

        _refreshTokenService.revokeToken(hashedToken.getToken());

        return new AuthTokens(newAccessToken, newRefreshToken);
    }
}
