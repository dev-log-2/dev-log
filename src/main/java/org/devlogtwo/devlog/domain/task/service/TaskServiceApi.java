package org.devlogtwo.devlog.domain.task.service;

import java.util.List;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.user.entity.User;

public interface TaskServiceApi {

    Task findTaskById(Long taskId);

    List<Task> findAllByTitleContainsOrDescriptionContains(String title, String description);
}
