package org.devlogtwo.devlog.domain.search.dto.response;

import org.devlogtwo.devlog.domain.team.entity.Team;

public record SearchTeamResponse(
        Long id,
        String name,
        String description
) {

    public static SearchTeamResponse from(Team team) {
        return new SearchTeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription());
    }
}
