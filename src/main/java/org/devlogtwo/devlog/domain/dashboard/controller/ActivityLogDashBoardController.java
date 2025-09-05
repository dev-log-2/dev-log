package org.devlogtwo.devlog.domain.dashboard.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.dto.PageResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.devlogtwo.devlog.domain.actlog.service.ActivityLogService;
import org.devlogtwo.devlog.domain.dashboard.dto.response.DashboardRecentActivityResponse;
import org.devlogtwo.devlog.domain.dashboard.service.ActivityLogDashboardService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActivityLogDashBoardController {

    private final ActivityLogDashboardService activityLogDashboardService;

    @GetMapping("/api/activities/my")
    public ResponseEntity<GlobalApiResponse<PageResponse<DashboardRecentActivityResponse>>> getRecentActivity(
            @PageableDefault(size = 10) Pageable pageable) {

        PageResponse<DashboardRecentActivityResponse> response = activityLogDashboardService.getRecentActivity(
                pageable);

        return ResponseHelper.success(SuccessCode.DASHBOARD_RECENT_ACTIVITY_SUCCESS, response);
    }

}
