package org.devlogtwo.devlog.domain.user.dto.response;

import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.entity.User;


public record UserAssignableResponse(
        Long id,
        String email,
        String name,
        UserRole role
) {
    public static UserAssignableResponse from(User user) {
        return new UserAssignableResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}
