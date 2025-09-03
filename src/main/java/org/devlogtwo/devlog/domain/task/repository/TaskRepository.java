package org.devlogtwo.devlog.domain.task.repository;

import org.devlogtwo.devlog.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
