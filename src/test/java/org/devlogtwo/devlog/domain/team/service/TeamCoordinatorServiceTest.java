package org.devlogtwo.devlog.domain.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.devlogtwo.devlog.common.type.TeamRole;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.team.dto.request.TeamUpdateRequest;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;
import org.devlogtwo.devlog.domain.team.dto.response.TeamResponse;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TeamCoordinatorServiceTest {

    @InjectMocks
    private TeamCoordinatorService teamCoordinatorService;

    @Mock
    private TeamServiceApi teamService;

    @Mock
    private TeamMemberServiceApi teamMemberService;

    @Test
    public void 단일_팀_조회() {
        //given
        long teamId = 1L;
        Team team = Team.createTeam("팀이름", "이것은 팀이다");

        List<User> users = createUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);

        TeamMember teamMember = TeamMember.addMember(user1, team, TeamRole.USER);
        TeamMember teamMember1 = TeamMember.addMember(user2, team, TeamRole.USER);

        List<TeamMemberResponse> members = Arrays.asList(
                TeamMemberResponse.from(teamMember),
                TeamMemberResponse.from(teamMember1)
        );

        ReflectionTestUtils.setField(team, "id", teamId);
        given(teamService.findById(teamId)).willReturn(team);
        given(teamMemberService.findTeamMembers(teamId)).willReturn(members);

        //when
        TeamResponse response = teamCoordinatorService.getTeam(teamId);

        //then
        verify(teamService).findById(teamId);
        verify(teamMemberService).findTeamMembers(teamId);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("팀이름");
        assertThat(response.members().size()).isEqualTo(2);
        assertThat(response.members().get(0).username()).isEqualTo(teamMember.getUser().getUsername());

    }

    @Test
    public void 팀_목록_조회() {
        //given
        List<Team> teams = createTeams();
        Team team1 = teams.get(0);
        Team team2 = teams.get(1);

        List<Long> teamIds = Arrays.asList(team1.getId(), team2.getId());

        List<User> users = createUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);

        TeamMember teamMember1 = TeamMember.addMember(user1, team1, TeamRole.ADMIN);
        TeamMember teamMember2 = TeamMember.addMember(user2, team1, TeamRole.USER);
        TeamMember teamMember3 = TeamMember.addMember(user3, team2, TeamRole.USER);
        List<TeamMember> members = Arrays.asList(
                teamMember1, teamMember2, teamMember3);

        given(teamService.findAll()).willReturn(teams);
        given(teamMemberService.findByTeamIds(teamIds)).willReturn(members);

        //when
        List<TeamResponse> teams1 = teamCoordinatorService.getTeams();

        //then
        assertThat(teams1.size()).isEqualTo(2);
        assertThat(teams1.get(0).members()).extracting(TeamMemberResponse::name)  // username()
                .contains("사용자이름1", "사용자이름2");
        assertThat(teams1.get(1).members())
                .extracting(TeamMemberResponse::name)
                .contains("사용자이름3");
    }

    @Test
    public void 팀_목록_조회_팀만있고_팀원없음() {
        //given
        List<Team> teams = createTeams();
        given(teamService.findAll()).willReturn(teams);
        given(teamMemberService.findByTeamIds(List.of(1L, 2L))).willReturn(List.of());

        //when
        List<TeamResponse> teams1 = teamCoordinatorService.getTeams();

        //then
        assertThat(teams1.size()).isEqualTo(2);
        assertThat(teams1.get(0).members()).isEmpty();

    }

    @Test
    public void 팀_업데이트_할_수_있음() {
        // given
        Long teamId = 1L;
        TeamUpdateRequest request = new TeamUpdateRequest("새이름", "새설명");
        Team mockTeam = mock(Team.class);

        given(teamService.findById(teamId)).willReturn(mockTeam);
        given(teamMemberService.findTeamMembers(teamId)).willReturn(List.of());

        // when
        teamCoordinatorService.updateTeam(teamId, request);

        // then
        verify(teamService).findById(teamId);
        verify(mockTeam).updateTeam("새이름", "새설명");
        verify(teamMemberService).findTeamMembers(teamId);
    }


    private List<Team> createTeams() {
        Team team1 = Team.createTeam("팀이름1", "이것은 팀이다");
        ReflectionTestUtils.setField(team1, "id", 1L);
        Team team2 = Team.createTeam("팀이름1", "이것은 팀이다");
        ReflectionTestUtils.setField(team2, "id", 2L);
        return Arrays.asList(team1, team2);
    }

    private List<User> createUsers() {
        User user1 = User.signUp("아이디1", "사용자이름1", "e@e", "Test1234!", UserRole.USER);
        ReflectionTestUtils.setField(user1, "id", 1L);
        User user2 = User.signUp("아이디2", "사용자이름2", "e2@e", "Test1234!", UserRole.USER);
        ReflectionTestUtils.setField(user2, "id", 2L);
        User user3 = User.signUp("아이디3", "사용자이름3", "e3@e", "Test1234!", UserRole.USER);
        ReflectionTestUtils.setField(user3, "id", 3L);
        return Arrays.asList(user1, user2, user3);
    }
}
