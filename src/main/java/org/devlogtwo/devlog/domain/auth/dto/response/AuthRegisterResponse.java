package org.devlogtwo.devlog.domain.auth.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.entity.User;

public record AuthRegisterResponse(
        Long id,
        String username,
        String email,
        String name,
        UserRole role,
        LocalDateTime createdAt
) {
    public static AuthRegisterResponse from(User user) {
        return new AuthRegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

}
