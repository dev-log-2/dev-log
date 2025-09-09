package org.devlogtwo.devlog.domain.task.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.TaskPriority;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.user.entity.User;

public record TaskResponse(
        Long id,
        String title,
        String description,
        LocalDateTime dueDate,
        TaskPriority priority,
        TaskStatus status,
        Long assigneeId,
        Assignee assignee,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public record Assignee(
            Long id,
            String username,
            String name,
            String email
    ) {
        public static Assignee from(User user) {
            if (user == null) {
                return null;
            }

            return new Assignee(user.getId(), user.getUsername(), user.getName(), user.getEmail());
        }
    }

    public static TaskResponse from(Task task) {
        User assignee = task.getAssignee();

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(),
                task.getPriority(), task.getStatus(), assignee != null ? assignee.getId() : null,
                Assignee.from(assignee), task.getCreatedAt(),
                task.getUpdatedAt());
    }
}
