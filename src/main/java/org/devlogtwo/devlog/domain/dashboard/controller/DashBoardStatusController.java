package org.devlogtwo.devlog.domain.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.security.UserPrincipal;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.dashboard.dto.response.DashboardStatsResponse;
import org.devlogtwo.devlog.domain.dashboard.service.DashboardStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashBoardStatusController {

    private final DashboardStatsService dashboardStatsService;

    //대시보드 통계 조회
    @GetMapping("/stats")
    public ResponseEntity<GlobalApiResponse<DashboardStatsResponse>> getDashboardStats(
            @AuthenticationPrincipal UserPrincipal authUser
    ) {
        DashboardStatsResponse stats = dashboardStatsService.getStats(authUser.id());

        return ResponseHelper.success(SuccessCode.DASHBOARD_STATS_FETCHED, stats);
    }


}

