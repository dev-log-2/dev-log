package org.devlogtwo.devlog.domain.user.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.entity.User;

public record UserDetailsResponse(
        Long id,
        String username,
        String email,
        String name,
        UserRole role,
        LocalDateTime createdAt
) {
    public static UserDetailsResponse from(User user) {
        return new UserDetailsResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
