package org.devlogtwo.devlog.domain.search.dto.response;

import java.util.List;

public record SearchResponse(
        List<SearchTaskResponse> tasks,
        List<SearchUserResponse> users,
        List<SearchTeamResponse> teams
) {

    public static SearchResponse of(List<SearchTaskResponse> tasks, List<SearchUserResponse> users,
                                    List<SearchTeamResponse> teams) {
        return new SearchResponse(tasks, users, teams);
    }
}
