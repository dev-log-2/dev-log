package org.devlogtwo.devlog.domain.team.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.type.TeamRole;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.team.dto.request.TeamMemberJoinRequest;
import org.devlogtwo.devlog.domain.team.dto.response.AvailableTeamMemberResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.devlogtwo.devlog.domain.team.repository.TeamMemberRepository;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    @InjectMocks
    private TeamMemberService teamMemberService;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private UserService userService;

    @Test
    public void 팀에_멤버_추가() {
        //given
        Long teamId = 1L;
        Long userId = 1L;

        given(teamService.existsById(teamId)).willReturn(true);

        given(teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)).willReturn(false);

        User foundUser = User.signUp("아이디", "사용자이름", "devlog@example.com", "Test1234!", UserRole.USER);
        given(userService.findUserById(userId)).willReturn(foundUser);

        Team foundTeam = Team.createTeam("팀이름", "이것은 팀이다");
        given(teamService.findById(teamId)).willReturn(foundTeam);

        TeamMember savedMember = TeamMember.addMember(foundUser, foundTeam, TeamRole.USER);
        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(savedMember);

        TeamMemberJoinRequest request = new TeamMemberJoinRequest(userId);

        //when
        teamMemberService.joinMember(teamId, request);

        //then
        verify(teamMemberRepository).existsByTeamIdAndUserId(teamId, userId);
        verify(userService).findUserById(userId);
        verify(teamService).findById(teamId);
        verify(teamMemberRepository).save(any(TeamMember.class));
    }

    @Test
    public void 팀에_멤버_추가_실패_이미존재() {
        //given
        Long teamId = 1L;
        Long userId = 1L;
        given(teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)).willReturn(true);

        //when & then
        assertThrows(CustomBusinessException.class, () -> {
            teamMemberService.joinMember(teamId, new TeamMemberJoinRequest(userId));
        });
    }

    @Test
    public void 팀에_멤버_추가_실패_팀없음() {
        //given
        Long teamId = 1L;
        Long userId = 1L;
        given(teamService.findById(teamId)).willThrow(new CustomBusinessException(ErrorCode.TEAM_NOT_FOUND));

        //when & then
        assertThrows(CustomBusinessException.class, () -> {
            teamMemberService.joinMember(teamId, new TeamMemberJoinRequest(userId));
        });
    }

    @Test
    public void 팀에_멤버_추가_실패_유저없음() {
        //given
        Long teamId = 1L;
        Long userId = 1L;
        given(userService.findUserById(userId)).willThrow(new CustomBusinessException(ErrorCode.USER_NOT_FOUND));

        //when & then
        assertThrows(CustomBusinessException.class, () -> {
            teamMemberService.joinMember(teamId, new TeamMemberJoinRequest(userId));
        });
    }

    @Test
    public void 팀에_추가_가능한_맴버조회() {
        //given
        Long teamId = 1L;
        List<User> availableUsers = Arrays.asList(
                User.signUp("아이디1", "사용자이름1", "e@e", "Test1234!", UserRole.USER),
                User.signUp("아이디2", "사용자이름2", "e2@e", "Test1234!", UserRole.USER)
        );

        given(teamService.existsById(teamId)).willReturn(true);
        given(userService.getAvailableUsersForTeam(teamId)).willReturn(availableUsers);

        //when
        List<AvailableTeamMemberResponse> result = teamMemberService.getAvailableTeamMembers(teamId);

        //then
        verify(userService).getAvailableUsersForTeam(teamId);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(availableUsers.size());
        assertThat(result.get(0).username()).isEqualTo(availableUsers.get(0).getUsername());
    }

    @Test
    public void 단일_팀의_멤버_조회() {
        //given
        Team team = Team.createTeam("팀이름", "이것은 팀이다");
        Long teamId = 1L;
        ReflectionTestUtils.setField(team, "id", teamId);

        User user1 = User.signUp("아이디1", "사용자이름1", "e@e", "Test1234!", UserRole.USER);
        ReflectionTestUtils.setField(user1, "id", 1L);
        User user2 = User.signUp("아이디2", "사용자이름2", "e2@e", "Test1234!", UserRole.USER);
        ReflectionTestUtils.setField(user2, "id", 2L);

        List<TeamMember> expectedMembers = Arrays.asList(
                TeamMember.addMember(user1, team, TeamRole.USER),
                TeamMember.addMember(user2, team, TeamRole.ADMIN)
        );

        given(teamService.existsById(teamId)).willReturn(true);
        given(teamMemberRepository.findByTeamId(teamId)).willReturn(expectedMembers);

        //when
        List<TeamMemberResponse> result = teamMemberService.findTeamMembers(teamId);

        //then
        verify(teamService).existsById(teamId);
        verify(teamMemberRepository).findByTeamId(teamId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(expectedMembers.size());
        assertThat(result.get(0).id()).isEqualTo(user1.getId());
    }

    @Test
    public void 여러_팀의_멤버_조회() {
        //given
        Team team1 = Team.createTeam("팀이름1", "이것은 팀이다1");
        ReflectionTestUtils.setField(team1, "id", 1L);
        Team team2 = Team.createTeam("팀이름2", "이것은 팀이다2");
        ReflectionTestUtils.setField(team2, "id", 2L);

        User user1 = User.signUp("아이디1", "사용자이름1", "e@e", "Test1234!", UserRole.USER);
        ReflectionTestUtils.setField(user1, "id", 1L);
        User user2 = User.signUp("아이디2", "사용자이름2", "e2@e", "Test1234!", UserRole.USER);
        ReflectionTestUtils.setField(user2, "id", 2L);

        List<TeamMember> expectedMembers = Arrays.asList(
                TeamMember.addMember(user1, team1, TeamRole.USER),
                TeamMember.addMember(user2, team2, TeamRole.ADMIN)
        );

        List<Long> teamIds = Arrays.asList(team1.getId(), team2.getId());

        given(teamMemberRepository.findByTeamIdIn(teamIds)).willReturn(expectedMembers);

        //when
        List<TeamMember> result = teamMemberService.findByTeamIds(teamIds);

        //then
        verify(teamMemberRepository).findByTeamIdIn(teamIds);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(expectedMembers.size());
        assertThat(result.get(0).getUser().getId()).isEqualTo(user1.getId());
    }


}
