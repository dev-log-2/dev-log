package org.devlogtwo.devlog.domain.team.service;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.team.dto.request.TeamCreateRequest;
import org.devlogtwo.devlog.domain.team.dto.response.TeamCreateResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService implements TeamServiceApi {
    private final TeamRepository teamRepository;
    private final TeamMemberServiceApi teamMemberServiceApi;

    @Transactional
    public TeamCreateResponse createTeam(TeamCreateRequest request) {

        Team team = Team.createTeam(request.name(), request.description());
        Team savedTeam = teamRepository.save(team);

        return TeamCreateResponse.of(savedTeam, Collections.emptyList());
    }
}
