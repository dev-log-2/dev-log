package org.devlogtwo.devlog.domain.dashboard.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.task.entity.Task;

public record MyTasksResponse(
        Long id,
        String title,
        TaskStatus status,
        LocalDateTime dueDate
) {
    public static MyTasksResponse from(Task task) {
        return new MyTasksResponse(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getDueDate()
        );
    }
}