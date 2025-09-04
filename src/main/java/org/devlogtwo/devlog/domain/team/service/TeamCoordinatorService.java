package org.devlogtwo.devlog.domain.team.service;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.team.dto.request.TeamUpdateRequest;
import org.devlogtwo.devlog.domain.team.dto.response.TeamDeleteResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamCoordinatorService {

    private final TeamServiceApi teamService;
    private final TeamMemberServiceApi teamMemberService;

    public TeamResponse getTeam(Long teamId) {
        Team team = teamService.findById(teamId);
        List<TeamMemberResponse> members = teamMemberService.findTeamMembers(teamId);
        return TeamResponse.of(team, members);
    }

    public List<TeamResponse> getTeams() {

        List<Team> teams = teamService.findAll();
        List<Long> teamIds = teams.stream()
                .map(Team::getId)
                .toList();

        List<TeamMember> allMembers = teamMemberService.findByTeamIds(teamIds);

        //팀별로 멤버 그룹핑 (팀ID → 멤버 목록)
        Map<Long, List<TeamMemberResponse>> membersByTeamId = allMembers.stream()
                .collect(Collectors.groupingBy(
                        teamMember -> teamMember.getTeam().getId(),    // 팀 ID로 그룹핑
                        Collectors.mapping(
                                TeamMemberResponse::from,
                                Collectors.toList()
                        )
                ));

        return teams.stream()
                .map(team -> TeamResponse.of(
                        team,
                        membersByTeamId.getOrDefault(team.getId(), List.of())
                ))
                .toList();
    }


    @Transactional
    public TeamResponse updateTeam(Long teamId, @Valid TeamUpdateRequest request) {
        Team team = teamService.findById(teamId);
        team.updateTeam(request.name(), request.description());
        return TeamResponse.of(team, teamMemberService.findTeamMembers(teamId));
    }

    @Transactional
    public TeamDeleteResponse deleteTeam(Long teamId) {
        Team foundTeam = teamService.findById(teamId);
        teamMemberService.deleteByTeamId(teamId);
        teamService.delete(foundTeam);
        return TeamDeleteResponse.of();
    }

    @Transactional
    public TeamResponse deleteMemberFromTeam(Long teamId, Long userId) {
        Team team = teamService.findById(teamId);

        teamMemberService.deleteByTeamIdAndUserId(teamId, userId);

        List<TeamMemberResponse> members = teamMemberService.findTeamMembers(teamId);
        return TeamResponse.of(team, members);
    }
}
