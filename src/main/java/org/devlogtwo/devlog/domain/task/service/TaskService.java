package org.devlogtwo.devlog.domain.task.service;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.task.dto.request.TaskCreateRequest;
import org.devlogtwo.devlog.domain.task.dto.response.TaskCreateResponse;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.repository.TaskRepository;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskServiceApi {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskCreateResponse createTask(TaskCreateRequest request) {

        // TODO: 담당자 ID 검증 로직 (User 도메인 구현 이후 구현할 예정)
        User assignee = null;

        Task task = Task.create(request.title(), request.description(), request.priority(), assignee,
                request.dueDate());

        Task savedTask = taskRepository.save(task);

        return TaskCreateResponse.from(savedTask);
    }
}
