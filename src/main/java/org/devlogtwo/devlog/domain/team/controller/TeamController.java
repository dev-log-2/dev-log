package org.devlogtwo.devlog.domain.team.controller;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.team.dto.request.TeamCreateRequest;
import org.devlogtwo.devlog.domain.team.dto.response.TeamResponse;
import org.devlogtwo.devlog.domain.team.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/api/teams")
    public ResponseEntity<GlobalApiResponse<TeamResponse>> createTeam(@RequestBody TeamCreateRequest request) {
        TeamResponse response = teamService.createTeam(request);
        return ResponseHelper.success(SuccessCode.TEAM_CREATE_SUCCESS, response);
    }
}
