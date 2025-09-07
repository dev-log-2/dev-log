package org.devlogtwo.devlog.domain.task.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"assignee"})
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"assignee"})
    Page<Task> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"assignee"})
    Optional<Task> findById(Long id);

    @EntityGraph(attributePaths = {"assignee"})
    List<Task> findAllByTitleContainsOrDescriptionContains(String title, String description);

    @EntityGraph(attributePaths = {"assignee"})
    Page<Task> findByTitleContainsOrDescriptionContains(String title, String description, Pageable pageable);

    long countByAssigneeAndStatus(User assignee, TaskStatus status);

    long countByAssignee(User assignee);

    long countByStatus(TaskStatus status);

    long countByDueDateBeforeAndStatusNot(LocalDateTime dueDate, TaskStatus status);

    long countByAssigneeIdAndDueDateBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<Task> findAllByAssignee_Id(Long assigneeId);
}
