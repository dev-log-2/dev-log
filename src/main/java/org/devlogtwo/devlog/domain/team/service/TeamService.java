package org.devlogtwo.devlog.domain.team.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.domain.team.dto.request.TeamCreateRequest;
import org.devlogtwo.devlog.domain.team.dto.response.TeamResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService implements TeamServiceApi {
    private final TeamRepository teamRepository;

    @Transactional
    public TeamResponse createTeam(TeamCreateRequest request) {
        if (teamRepository.existsByName(request.name())) {
            throw new CustomBusinessException(ErrorCode.TEAM_ALREADY_EXISTS);
        }

        Team team = Team.createTeam(request.name(), request.description());
        Team savedTeam = teamRepository.save(team);

        return TeamResponse.of(savedTeam, Collections.emptyList());
    }


    @Override
    public Team findById(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new CustomBusinessException(ErrorCode.TEAM_NOT_FOUND));
    }

    @Override
    public List<Team> findAll() {
        return teamRepository.findAll();
    }


}
