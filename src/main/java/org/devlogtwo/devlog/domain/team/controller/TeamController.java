package org.devlogtwo.devlog.domain.team.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.team.dto.request.TeamCreateRequest;
import org.devlogtwo.devlog.domain.team.dto.response.TeamResponse;
import org.devlogtwo.devlog.domain.team.service.TeamQueryService;
import org.devlogtwo.devlog.domain.team.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamQueryService teamQueryService;

    @PostMapping("/api/teams")
    public ResponseEntity<GlobalApiResponse<TeamResponse>> createTeam(@RequestBody TeamCreateRequest request) {
        TeamResponse response = teamService.createTeam(request);
        return ResponseHelper.success(SuccessCode.TEAM_CREATE_SUCCESS, response);
    }

    @GetMapping("/api/teams")
    public ResponseEntity<GlobalApiResponse<List<TeamResponse>>> getTeams() {
        List<TeamResponse> response = teamQueryService.getTeams();
        return ResponseHelper.success(SuccessCode.TEAM_LIST_SUCCESS, response);
    }
}
