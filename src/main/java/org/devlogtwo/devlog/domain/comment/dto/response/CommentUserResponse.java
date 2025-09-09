package org.devlogtwo.devlog.domain.comment.dto.response;

import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.entity.User;


public record CommentUserResponse(Long id,
                                  String username,
                                  String name,
                                  String email,
                                  UserRole role) {

    public static CommentUserResponse from(User user) {
        return new CommentUserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
