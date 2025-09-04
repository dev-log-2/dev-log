package org.devlogtwo.devlog.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.security.UserPrincipal;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.user.dto.response.UserDetailsResponse;
import org.devlogtwo.devlog.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/users/me")
    public ResponseEntity<GlobalApiResponse<UserDetailsResponse>> getMyDetails(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        UserDetailsResponse response = userService.getUserDetails(currentUser.id());
        return ResponseHelper.success(SuccessCode.GET_USER_INFO_SUCCESS, response);
    }

}
