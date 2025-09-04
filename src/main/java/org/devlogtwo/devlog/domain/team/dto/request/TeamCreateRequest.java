package org.devlogtwo.devlog.domain.team.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeamCreateRequest(
        @NotBlank(message = "팀 이름은 필수입니다.")
        String name,
        String description
) {
}
