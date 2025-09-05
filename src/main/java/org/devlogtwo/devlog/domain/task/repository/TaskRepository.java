package org.devlogtwo.devlog.domain.task.repository;

import java.util.List;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    List<Task> findAllByTitleContainsOrDescriptionContains(String title, String description);

    Page<Task> findByTitleContainsOrDescriptionContains(String title, String description, Pageable pageable);
}
