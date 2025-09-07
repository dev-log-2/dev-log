package org.devlogtwo.devlog.domain.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.TaskPriority;
import org.devlogtwo.devlog.common.type.TaskStatus;

public record TaskUpdateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        String description,

        LocalDateTime dueDate,

        @NotNull(message = "우선순위는 필수입니다.")
        TaskPriority priority,

        @NotNull(message = "상태 값은 필수입니다.")
        TaskStatus status,

        @NotNull(message = "담당자는 필수입니다.")
        Long assigneeId
) {
}
