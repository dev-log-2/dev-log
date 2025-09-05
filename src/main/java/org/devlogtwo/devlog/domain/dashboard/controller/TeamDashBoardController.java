package org.devlogtwo.devlog.domain.dashboard.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.dashboard.service.TeamDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamDashBoardController {

    private final TeamDashboardService dashBoardService;

    @GetMapping("/api/dashboard/team-progress")
    public ResponseEntity<?> getTeamProgress() {
        Map<String, Integer> response = dashBoardService.getTeamProgress();

        return ResponseHelper.success(SuccessCode.DASHBOARD_TEAM_PROGRESS_SUCCESS, response);
    }
}
