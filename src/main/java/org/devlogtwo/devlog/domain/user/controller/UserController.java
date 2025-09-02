package org.devlogtwo.devlog.domain.user.controller;

import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalApiResponse<String> createUserOne() {
        return GlobalApiResponse.success(SuccessCode.SIGNUP_SUCCESS, "User created successfully");
    }


    @PostMapping("/api/v1/users")
    public ResponseEntity<GlobalApiResponse<String>> createUserTwo() {
        return ResponseHelper.success(SuccessCode.SIGNUP_SUCCESS, "User created successfully");
    }
}
