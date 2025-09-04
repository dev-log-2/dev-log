package org.devlogtwo.devlog.domain.search.dto.response;

import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.task.dto.response.TaskResponse;
import org.devlogtwo.devlog.domain.task.dto.response.TaskResponse.Assignee;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.user.entity.User;

public record SearchTaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Assignee assignee
) {

    public record Assignee(
            Long id,
            String name
    ) {
        public static Assignee from(User user) {
            if (user == null) {
                return null;
            }

            return new Assignee(user.getId(), user.getName());
        }
    }

    public static SearchTaskResponse from(Task task) {
        return new SearchTaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                Assignee.from(task.getAssignee()));
    }
}

