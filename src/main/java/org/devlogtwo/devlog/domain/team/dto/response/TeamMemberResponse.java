package org.devlogtwo.devlog.domain.team.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.TeamRole;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.devlogtwo.devlog.domain.user.entity.User;

public record TeamMemberResponse(
        Long id,
        String username,
        String name,
        String email,
        TeamRole role,
        LocalDateTime createdAt
) {
    public static TeamMemberResponse from(TeamMember teamMember) {
        User user = teamMember.getUser();
        return new TeamMemberResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                teamMember.getTeamRole(),
                teamMember.getCreatedAt()
        );
    }
}
