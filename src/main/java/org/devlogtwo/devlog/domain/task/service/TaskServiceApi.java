package org.devlogtwo.devlog.domain.task.service;

import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.user.entity.User;

public interface TaskServiceApi {

    Task findTaskById(Long taskId);
}
