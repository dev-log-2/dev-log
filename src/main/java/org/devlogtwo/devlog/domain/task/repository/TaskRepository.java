package org.devlogtwo.devlog.domain.task.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    List<Task> findAllByTitleContainsOrDescriptionContains(String title, String description);

    Page<Task> findByTitleContainsOrDescriptionContains(String title, String description, Pageable pageable);

    long countByAssigneeAndStatus(User assignee, TaskStatus status);

    long countByAssignee(User assignee);

    long countByStatus(TaskStatus status);

    long countByDueDateBeforeAndStatusNot(LocalDateTime dueDate, TaskStatus status);

    long countByAssigneeIdAndDueDateBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<Task> findAllByAssignee(Long assigneeId);
}
