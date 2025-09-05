package org.devlogtwo.devlog.domain.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.security.UserPrincipal;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.dashboard.dto.response.MyTasksSummaryResponse;
import org.devlogtwo.devlog.domain.dashboard.service.MyTaskDashBoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyTaskDashBoardController {

    private final MyTaskDashBoardService myTaskDashBoardService;

    @GetMapping("/api/dashboard/my-tasks")
    public ResponseEntity<GlobalApiResponse<MyTasksSummaryResponse>> getMyTasksByAssignee(
            @AuthenticationPrincipal UserPrincipal authUser) {
        MyTasksSummaryResponse response = myTaskDashBoardService.getMyTasks(authUser.id());
        return ResponseHelper.success(SuccessCode.DASHBOARD_MY_TASKS_SUCCESS, response);
    }
}

