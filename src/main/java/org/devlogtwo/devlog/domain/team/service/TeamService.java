package org.devlogtwo.devlog.domain.team.service;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.team.dto.request.TeamCreateRequest;
import org.devlogtwo.devlog.domain.team.dto.response.TeamCreateResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.repository.TeamRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService implements TeamServiceApi {
    private final TeamRepository teamRepository;
    private final TeamMemberServiceApi teamMemberService;

    public TeamCreateResponse createTeam(TeamCreateRequest request) {

        Team team = Team.of(request.name(), request.description());
        Team savedTeam = teamRepository.save(team);

        //TODO: 팀으로 멤버 조회 받아오기<-TeamMember?

        teamRepository.save(team);
        return TeamCreateResponse.of(savedTeam, null);
    }
}
