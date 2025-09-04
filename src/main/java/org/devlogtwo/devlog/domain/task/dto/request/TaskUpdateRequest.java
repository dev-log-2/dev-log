package org.devlogtwo.devlog.domain.task.dto.request;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.TaskPriority;
import org.devlogtwo.devlog.common.type.TaskStatus;

public record TaskUpdateRequest(
        String title,

        String description,

        LocalDateTime dueDate,

        TaskPriority priority,

        TaskStatus status,

        Long assigneeId
) {
}
