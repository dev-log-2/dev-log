package org.devlogtwo.devlog.common.security;

import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.entity.User;

/**
 * 비밀번호를 제외한 User 정보만 담아서 사용하기 위한 클래스 @AuthenticationPrincipal로 접근
 *
 * @param id
 * @param username
 * @param role
 */
public record UserPrincipal(
        Long id,
        String username,
        UserRole role
) {
    public static UserPrincipal from(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
