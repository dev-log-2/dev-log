package org.devlogtwo.devlog.domain.team.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.devlogtwo.devlog.domain.team.dto.request.TeamCreateRequest;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private TeamRepository teamRepository;

    @Test
    public void 팀을_만들_수_있다() {
        //given
        String name = "팀이름";
        String description = "이것은 팀이다";
        TeamCreateRequest request = new TeamCreateRequest(name, description);

        given(teamRepository.existsByName(name)).willReturn(false);
        given(teamRepository.save(any(Team.class))).willReturn(Team.createTeam(name, description));

        //when
        teamService.createTeam(request);

        //then
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    public void 팀을_Id로_조회할_수_있다() {
        //given
        Long teamId = 1L;
        Team team = Team.createTeam("팀이름", "이것은 팀이다");

        given(teamRepository.findById(teamId)).willReturn(Optional.of(team));

        //when
        Team result = teamService.findById(teamId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("팀이름");
    }

    @Test
    public void 팀을_전체_조회할_수_있다() {
        //given
        Team team1 = Team.createTeam("팀이름", "이것은 팀이다");
        Team team2 = Team.createTeam("팀이름2", "이것은 팀이다2");

        given(teamRepository.findAll()).willReturn(List.of(team1, team2));

        //when
        List<Team> result = teamService.findAll();

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void 팀이_존재하는지_확인할_수_있다() {
        //given
        Long teamId = 1L;

        given(teamRepository.existsById(teamId)).willReturn(true);

        //when
        boolean exists = teamService.existsById(teamId);

        //then
        assertThat(exists).isTrue();
    }

    @Test
    public void 팀을_삭제할_수_있다() {
        //given
        Team team = Team.createTeam("팀이름", "이것은 팀이다");

        //when
        teamService.delete(team);

        //then
        verify(teamRepository).delete(team);
    }

    @Test
    public void 팀을_이름이나_설명으로_검색할_수_있다() {
        //given
        String name = "팀이름";
        String description = "이것은";
        Team team1 = Team.createTeam("팀이름", "이것은 팀이다");
        Team team2 = Team.createTeam("팀이름2", "이것은 팀이다2");
        given(teamRepository.findAllByNameContainsOrDescriptionContains(name, description))
                .willReturn(List.of(team1, team2));
        //when
        List<Team> result = teamService.findAllByNameContainsOrDescriptionContains(name, description);
        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }
}
