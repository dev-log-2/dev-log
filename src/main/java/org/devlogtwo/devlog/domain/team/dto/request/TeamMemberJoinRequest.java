package org.devlogtwo.devlog.domain.team.dto.request;

import jakarta.validation.constraints.NotNull;

public record TeamMemberJoinRequest(
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId
) {
}
