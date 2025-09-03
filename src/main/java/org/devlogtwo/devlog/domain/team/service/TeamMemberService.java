package org.devlogtwo.devlog.domain.team.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.devlogtwo.devlog.domain.team.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService implements TeamMemberServiceApi {
    private final TeamMemberRepository teamMemberRepository;


    @Override
    public List<TeamMemberResponse> findTeamMembers(Long teamId) {

        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);

        return members.stream().map(TeamMemberResponse::from).toList();
    }
}
