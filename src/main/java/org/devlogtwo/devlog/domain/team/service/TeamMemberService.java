package org.devlogtwo.devlog.domain.team.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.type.TeamRole;
import org.devlogtwo.devlog.domain.team.dto.request.TeamMemberJoinRequest;
import org.devlogtwo.devlog.domain.team.dto.response.AvailableTeamMemberResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.devlogtwo.devlog.domain.team.repository.TeamMemberRepository;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamMemberService implements TeamMemberServiceApi {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamServiceApi teamService;
    private final UserServiceApi userService;

    @Transactional
    public TeamResponse joinMember(Long teamId, TeamMemberJoinRequest request) {
        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, request.userId())) {
            throw new CustomBusinessException(ErrorCode.TEAM_MEMBER_ALREADY_EXISTS);
        }
        User foundUser = userService.findUserById(request.userId());
        Team foundTeam = teamService.findById(teamId);
        TeamMember teamMember = TeamMember.addMember(foundUser, foundTeam, TeamRole.USER);
        teamMemberRepository.save(teamMember);
        List<TeamMemberResponse> teamMembers = findTeamMembers(teamId);
        return TeamResponse.of(foundTeam, teamMembers);
    }

    @Transactional(readOnly = true)
    public List<AvailableTeamMemberResponse> getAvailableTeamMembers(Long teamId) {

        if (!teamService.existsById(teamId)) {
            throw new CustomBusinessException(ErrorCode.TEAM_NOT_FOUND);
        }

        List<User> availableUsersForTeam = userService.getAvailableUsersForTeam(teamId);

        return availableUsersForTeam.stream()
                .map(AvailableTeamMemberResponse::from)
                .toList();

    }

    //단일 팀의 멤버 조회
    @Override
    public List<TeamMemberResponse> findTeamMembers(Long teamId) {

        if (!teamService.existsById(teamId)) {
            throw new CustomBusinessException(ErrorCode.TEAM_NOT_FOUND);
        }

        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);

        return members.stream().map(TeamMemberResponse::from).toList();
    }

    //여러 팀의 멤버 조회
    @Override
    public List<TeamMember> findByTeamIds(List<Long> teamIds) {
        return teamMemberRepository.findByTeamIdIn(teamIds);
    }

    @Override
    public void deleteByTeamId(Long teamId) {
        teamMemberRepository.deleteByTeamId(teamId);
    }

    @Override
    public void deleteByTeamIdAndUserId(Long teamId, Long userId) {
        teamMemberRepository.deleteByTeamIdAndUserId(teamId, userId);
    }


}
