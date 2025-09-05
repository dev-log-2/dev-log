package org.devlogtwo.devlog.domain.search.dto.response;

import org.devlogtwo.devlog.domain.user.entity.User;

public record SearchUserResponse(
        Long id,
        String username,
        String name,
        String email
) {

    public static SearchUserResponse from(User user) {
        return new SearchUserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail());
    }
}
