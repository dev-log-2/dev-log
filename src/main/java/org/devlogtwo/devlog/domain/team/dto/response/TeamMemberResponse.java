package org.devlogtwo.devlog.domain.team.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.UserRole;

public record TeamMemberResponse(
        Long id,
        String username,
        String email,
        UserRole role,
        LocalDateTime createdAt
) {
}
