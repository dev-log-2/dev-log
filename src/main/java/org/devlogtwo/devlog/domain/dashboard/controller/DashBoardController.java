package org.devlogtwo.devlog.domain.dashboard.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.dto.PageResponse;
import org.devlogtwo.devlog.common.security.UserPrincipal;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.dashboard.dto.response.DashboardRecentActivityResponse;
import org.devlogtwo.devlog.domain.dashboard.dto.response.DashboardStatsResponse;
import org.devlogtwo.devlog.domain.dashboard.dto.response.MyTasksSummaryResponse;
import org.devlogtwo.devlog.domain.dashboard.service.DashBoardService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashBoardController {
    private final DashBoardService dashBoardService;

    //대시보드 통계 조회
    @GetMapping("/api/dashboard/stats")
    public ResponseEntity<GlobalApiResponse<DashboardStatsResponse>> getDashboardStats(
            @AuthenticationPrincipal UserPrincipal authUser
    ) {
        DashboardStatsResponse stats = dashBoardService.getStats(authUser.id());

        return ResponseHelper.success(SuccessCode.DASHBOARD_STATS_FETCHED, stats);
    }

    @GetMapping("/api/dashboard/my-tasks")
    public ResponseEntity<GlobalApiResponse<MyTasksSummaryResponse>> getMyTasksByAssignee(
            @AuthenticationPrincipal UserPrincipal authUser) {
        MyTasksSummaryResponse response = dashBoardService.getMyTasks(authUser.id());
        return ResponseHelper.success(SuccessCode.DASHBOARD_MY_TASKS_SUCCESS, response);
    }

    @GetMapping("/api/dashboard/team-progress")
    public ResponseEntity<?> getTeamProgress() {
        Map<String, Integer> response = dashBoardService.getTeamProgress();

        return ResponseHelper.success(SuccessCode.DASHBOARD_TEAM_PROGRESS_SUCCESS, response);
    }

    @GetMapping("/api/activities/my")
    public ResponseEntity<GlobalApiResponse<PageResponse<DashboardRecentActivityResponse>>> getRecentActivity(
            @PageableDefault(size = 10) Pageable pageable) {

        PageResponse<DashboardRecentActivityResponse> response = dashBoardService.getRecentActivity(
                pageable);

        return ResponseHelper.success(SuccessCode.DASHBOARD_RECENT_ACTIVITY_SUCCESS, response);
    }


}
