package org.devlogtwo.devlog.domain.team.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import org.devlogtwo.devlog.domain.team.entity.Team;

public record TeamCreateResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        List<TeamMemberResponse> members
) {
    public static TeamCreateResponse of(Team team, List<TeamMemberResponse> members) {
        return new TeamCreateResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getCreatedAt(),
                members
        );
    }
}
