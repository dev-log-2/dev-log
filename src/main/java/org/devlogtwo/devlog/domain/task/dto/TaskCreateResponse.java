package org.devlogtwo.devlog.domain.task.dto;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.TaskPriority;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.task.entity.Task;

public record TaskCreateResponse(
        Long id,
        String title,
        String description,
        LocalDateTime dueDate,
        TaskPriority priority,
        TaskStatus status,
        Long assigneeId,
        String assigneeUserName,
        String assigneeName
) {
    public static TaskCreateResponse from(Task task) {
        return new TaskCreateResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus(),
                task.getAssignee().getId(),
                task.getAssignee().getUsername(),
                task.getAssignee().getName()
        );
    }
}
