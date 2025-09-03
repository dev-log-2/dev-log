package org.devlogtwo.devlog.domain.task.service;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.domain.task.dto.request.TaskCreateRequest;
import org.devlogtwo.devlog.domain.task.dto.response.TaskCreateResponse;
import org.devlogtwo.devlog.domain.task.dto.response.TaskResponse;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.repository.TaskRepository;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskServiceApi {

    private final TaskRepository taskRepository;
    private final UserServiceApi userServiceApi;

    @Transactional
    public TaskCreateResponse createTask(TaskCreateRequest request) {

        // 담당자 ID 검증 로직
        User assignee = userServiceApi.findUserById(request.assigneeId());

        Task task = Task.create(request.title(), request.description(), request.priority(), assignee,
                request.dueDate());

        Task savedTask = taskRepository.save(task);

        return TaskCreateResponse.from(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.TASK_NOT_FOUND));

        return TaskResponse.from(task);

    }
}
