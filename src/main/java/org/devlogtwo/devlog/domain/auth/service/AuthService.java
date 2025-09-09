package org.devlogtwo.devlog.domain.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.security.JwtTokenProvider;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.auth.dto.request.AuthLoginRequest;
import org.devlogtwo.devlog.domain.auth.dto.request.AuthRegisterRequest;
import org.devlogtwo.devlog.domain.auth.dto.request.AuthWithdrawRequest;
import org.devlogtwo.devlog.domain.auth.dto.response.AuthLoginResponse;
import org.devlogtwo.devlog.domain.auth.dto.response.AuthRegisterResponse;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceApi userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthRegisterResponse signUp(AuthRegisterRequest authRegisterRequest) {

        if (userService.isUsernameTaken(authRegisterRequest.username())) {
            throw new CustomBusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        if (userService.isEmailTaken(authRegisterRequest.email())) {
            throw new CustomBusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(authRegisterRequest.password());
        User newUser = User.signUp(authRegisterRequest.username(), authRegisterRequest.name(),
                authRegisterRequest.email(), encodedPassword, UserRole.USER);

        User savedUser = userService.registerNewUser(newUser);

        return AuthRegisterResponse.from(savedUser);
    }

    @Transactional
    public AuthLoginResponse login(AuthLoginRequest authLoginRequest) {

        User user = userService.findUserByUsername(authLoginRequest.username());

        if (!passwordEncoder.matches(authLoginRequest.password(), user.getPassword())) {
            throw new CustomBusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtTokenProvider.createToken(user.getUsername());
        return AuthLoginResponse.from(token);
    }

    @Transactional
    public void withdraw(@Valid AuthWithdrawRequest authWithdrawRequest, Long userId) {

        User user = userService.findUserById(userId);
        if (!passwordEncoder.matches(authWithdrawRequest.password(), user.getPassword())) {
            throw new CustomBusinessException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        userService.deleteUser(user);
    }
}
