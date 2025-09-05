package org.devlogtwo.devlog.domain.search.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.search.dto.response.SearchResponse;
import org.devlogtwo.devlog.domain.search.dto.response.SearchTaskResponse;
import org.devlogtwo.devlog.domain.search.dto.response.SearchTeamResponse;
import org.devlogtwo.devlog.domain.search.dto.response.SearchUserResponse;
import org.devlogtwo.devlog.domain.task.service.TaskServiceApi;
import org.devlogtwo.devlog.domain.team.service.TeamServiceApi;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserServiceApi userServiceApi;
    private final TeamServiceApi teamServiceApi;
    private final TaskServiceApi taskServiceApi;

    public SearchResponse searchAll(String keyword) {

        List<SearchTaskResponse> tasks = taskServiceApi.findAllByTitleContainsOrDescriptionContains(keyword, keyword)
                .stream()
                .map(SearchTaskResponse::from)
                .collect(Collectors.toList());

        List<SearchTeamResponse> teams  = teamServiceApi.findAllByNameContainsOrDescriptionContains(keyword, keyword)
                .stream()
                .map(SearchTeamResponse::from)
                .collect(Collectors.toList());

        List<SearchUserResponse> users = userServiceApi.findAllByUsernameContainsOrNameContains(keyword, keyword)
                .stream()
                .map(SearchUserResponse::from)
                .collect(Collectors.toList());

        return SearchResponse.of(tasks, users, teams);
    }
}
