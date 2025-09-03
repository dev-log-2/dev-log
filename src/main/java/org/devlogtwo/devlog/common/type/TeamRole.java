package org.devlogtwo.devlog.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamRole {
    LEADER("ROLE_LEADER", "팀장"),
    USER("ROLE_MEMBER", "팀원"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String authority;
    private final String description;
}
