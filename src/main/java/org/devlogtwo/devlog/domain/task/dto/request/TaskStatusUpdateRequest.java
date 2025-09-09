package org.devlogtwo.devlog.domain.task.dto.request;

import jakarta.validation.constraints.NotNull;
import org.devlogtwo.devlog.common.type.TaskStatus;

public record TaskStatusUpdateRequest(
        @NotNull(message = "상태 값은 필수입니다.")
        TaskStatus status
) {
}
