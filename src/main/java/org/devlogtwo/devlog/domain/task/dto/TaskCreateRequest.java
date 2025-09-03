package org.devlogtwo.devlog.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.TaskPriority;

public record TaskCreateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        String description,

        LocalDateTime dueDate,

        @NotBlank(message = "우선순위는 필수입니다.")
        TaskPriority priority,

        @NotBlank(message = "담당자는 필수입니다.")
        Long assigneeId
) {
}
