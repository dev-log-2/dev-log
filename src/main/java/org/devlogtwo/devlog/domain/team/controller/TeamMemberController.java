package org.devlogtwo.devlog.domain.team.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.team.dto.request.TeamMemberJoinRequest;
import org.devlogtwo.devlog.domain.team.dto.response.AvailableTeamMemberResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamResponse;
import org.devlogtwo.devlog.domain.team.service.TeamMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @PostMapping("/api/teams/{teamId}/members")
    public ResponseEntity<GlobalApiResponse<TeamResponse>> joinMember(@PathVariable Long teamId,
                                                                      @Valid @RequestBody TeamMemberJoinRequest request
    ) {
        TeamResponse response = teamMemberService.joinMember(teamId, request);
        return ResponseHelper.success(SuccessCode.TEAM_MEMBER_JOIN_SUCCESS, response);
    }

    @GetMapping("/api/teams/{teamId}/members")
    public ResponseEntity<GlobalApiResponse<List<TeamMemberResponse>>> findTeamMembers(@PathVariable Long teamId) {
        List<TeamMemberResponse> response = teamMemberService.findTeamMembers(teamId);
        return ResponseHelper.success(SuccessCode.TEAM_MEMBER_LIST_SUCCESS, response);
    }

    @GetMapping("/api/users/available")
    public ResponseEntity<GlobalApiResponse<List<AvailableTeamMemberResponse>>> getAvailableTeamMembers(
            @RequestParam Long teamId) {
        List<AvailableTeamMemberResponse> response = teamMemberService.getAvailableTeamMembers(teamId);
        return ResponseHelper.success(SuccessCode.TEAM_AVAILABLE_MEMBER_LIST_SUCCESS, response);
    }
}
