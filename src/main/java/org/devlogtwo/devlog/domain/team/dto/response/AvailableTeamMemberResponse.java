package org.devlogtwo.devlog.domain.team.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.entity.User;

public record AvailableTeamMemberResponse(
        Long id,
        String username,
        String name,
        String email,
        UserRole role,
        LocalDateTime createdAt
) {
    public static AvailableTeamMemberResponse from(User user) {
        return new AvailableTeamMemberResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
