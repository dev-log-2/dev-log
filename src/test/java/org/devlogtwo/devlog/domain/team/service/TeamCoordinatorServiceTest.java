package org.devlogtwo.devlog.domain.team.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.devlogtwo.devlog.common.type.TeamRole;
import org.devlogtwo.devlog.common.type.UserRole;
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

        User user1 = User.signUp("아이디1", "사용자이름1", "e@e", "Test1234!", UserRole.USER);
        ReflectionTestUtils.setField(user1, "id", 1L);
        User user2 = User.signUp("아이디2", "사용자이름2", "e2@e", "Test1234!", UserRole.USER);
        ReflectionTestUtils.setField(user2, "id", 2L);

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
}
