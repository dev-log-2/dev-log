package org.devlogtwo.devlog.domain.team.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {

    private final TeamServiceApi teamService;
    private final TeamMemberServiceApi teamMemberService;


    @Transactional(readOnly = true)
    public List<TeamResponse> getTeams() {
        List<TeamResponse> responses = new ArrayList<>();
        List<Team> all = teamService.findAll(); // teamRepository 대신 teamService 사용

        for (Team team : all) {
            List<TeamMemberResponse> teamMembers = teamMemberService.findTeamMembers(team.getId());
            responses.add(TeamResponse.of(team, teamMembers));
        }
        return responses;
    }
}
