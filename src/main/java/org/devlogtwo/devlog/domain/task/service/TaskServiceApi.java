package org.devlogtwo.devlog.domain.task.service;

import java.time.LocalDateTime;
import java.util.List;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.dashboard.dto.response.TaskDailySummaryResponse;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.user.entity.User;

public interface TaskServiceApi {

    Task findTaskById(Long taskId);

    List<Task> findAllByTitleContainsOrDescriptionContains(String title, String description);

    long countByAssigneeAndStatus(User assignee, TaskStatus status);

    long countByAssignee(User assignee);

    long count();

    long countByStatus(TaskStatus status);

    long countByDueDateBeforeAndStatusNot(LocalDateTime dueDate, TaskStatus status);

    long countByAssigneeIdAndDueDateBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<Task> findAllByAssignee_Id(Long assigneeId);

    List<TaskDailySummaryResponse> findWeeklyTaskSummary(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
