package org.devlogtwo.devlog.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.auth.dto.request.AuthLoginRequest;
import org.devlogtwo.devlog.domain.auth.dto.request.AuthRegisterRequest;
import org.devlogtwo.devlog.domain.auth.dto.response.AuthLoginResponse;
import org.devlogtwo.devlog.domain.auth.dto.response.AuthRegisterResponse;
import org.devlogtwo.devlog.domain.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/auth/register")
    public ResponseEntity<GlobalApiResponse<AuthRegisterResponse>> register(
            @Valid @RequestBody AuthRegisterRequest authRegisterRequest) {

        AuthRegisterResponse authRegisterResponse = authService.signUp(authRegisterRequest);
        return ResponseHelper.success(SuccessCode.SIGNUP_SUCCESS, authRegisterResponse);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<GlobalApiResponse<AuthLoginResponse>> login(
            @Valid @RequestBody AuthLoginRequest authLoginRequest) {

        AuthLoginResponse authLoginResponse = authService.login(authLoginRequest);
        return ResponseHelper.success(SuccessCode.LOGIN_SUCCESS, authLoginResponse);
    }
}
