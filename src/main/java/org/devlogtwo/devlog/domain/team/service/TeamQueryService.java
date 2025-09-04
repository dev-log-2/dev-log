package org.devlogtwo.devlog.domain.team.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {

    private final TeamServiceApi teamService;
    private final TeamMemberServiceApi teamMemberService;

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
}
