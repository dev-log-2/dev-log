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
import org.devlogtwo.devlog.domain.user.repository.UserRepository;
import org.devlogtwo.devlog.domain.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthRegisterResponse signUp(AuthRegisterRequest authRegisterRequest) {

        // username 중복 검증
        if (userRepository.existsByUsername(authRegisterRequest.username())) {
            throw new CustomBusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        // email 중복 검증
        if (userRepository.existsByEmail(authRegisterRequest.email())) {
            throw new CustomBusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호, 비밀번호 확인 일치 여부 검증
        String password = authRegisterRequest.password();
        String passwordConfirm = authRegisterRequest.passwordConfirm();

        if (!password.equals(passwordConfirm)) {
            throw new CustomBusinessException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User newUser = User.signUp(authRegisterRequest.username(), authRegisterRequest.name(),
                authRegisterRequest.email(), encodedPassword, UserRole.USER);

        User savedUser = userRepository.save(newUser);

        return AuthRegisterResponse.from(savedUser);
    }

    public AuthLoginResponse login(AuthLoginRequest authLoginRequest) {

        // username에 의한 user 존재 여부 검증
        User user = userService.findUserByUsername(authLoginRequest.username());

        // password 일치 여부 검증
        if (!passwordEncoder.matches(authLoginRequest.password(), user.getPassword())) {
            throw new CustomBusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // token 생성
        String token = jwtTokenProvider.createToken(user.getUsername());
        return AuthLoginResponse.from(token);
    }

    public void withdraw(@Valid AuthWithdrawRequest authWithdrawRequest, Long userId) {

        // 인증된 유저의 비밀번호와 입력된 비밀번호 다시 확인
        User user = userService.findUserById(userId);
        if (!passwordEncoder.matches(authWithdrawRequest.password(), user.getPassword())) {
            throw new CustomBusinessException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        userRepository.delete(user);
    }
}
