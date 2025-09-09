package org.devlogtwo.devlog.domain.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;
import org.devlogtwo.devlog.common.type.TaskPriority;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.search.dto.response.SearchResponse;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.service.TaskServiceApi;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.service.TeamServiceApi;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private TaskServiceApi taskServiceApi;

    @Mock
    private UserServiceApi userServiceApi;

    @Mock
    private TeamServiceApi teamServiceApi;

    @InjectMocks
    private SearchService searchService;

    @Test
    @DisplayName("통합 검색 성공 테스트")
    void searchAll() {
        // given
        String query = "검색어";
        User user = User.signUp("test", "테스트", "test@test.com", "password", UserRole.USER);
        Task task = Task.create("제목", "설명", TaskPriority.HIGH, user, LocalDateTime.now().plusDays(1));
        Team team = Team.createTeam("개발팀", "팀 설명");

        given(taskServiceApi.findAllByTitleContainsOrDescriptionContains(query, query))
                .willReturn(List.of(task));
        given(teamServiceApi.findAllByNameContainsOrDescriptionContains(query, query))
                .willReturn(List.of(team));
        given(userServiceApi.findAllByUsernameContainsOrNameContains(query, query))
                .willReturn(List.of(user));

        // when
        SearchResponse response = searchService.searchAll(query);

        // then
        assertThat(response).isNotNull();

        // task 검색 결과
        assertThat(response.tasks()).hasSize(1);
        assertThat(response.tasks().get(0).id()).isEqualTo(task.getId());
        assertThat(response.tasks().get(0).title()).isEqualTo(task.getTitle());
        assertThat(response.tasks().get(0).description()).isEqualTo(task.getDescription());

        // user 검색 결과
        assertThat(response.users()).hasSize(1);
        assertThat(response.users().get(0).id()).isEqualTo(user.getId());
        assertThat(response.users().get(0).name()).isEqualTo(user.getName());

        // team 검색 결과
        assertThat(response.teams()).hasSize(1);
        assertThat(response.teams().get(0).id()).isEqualTo(team.getId());
        assertThat(response.teams().get(0).name()).isEqualTo(team.getName());
    }
}
