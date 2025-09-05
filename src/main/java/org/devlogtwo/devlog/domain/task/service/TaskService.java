package org.devlogtwo.devlog.domain.task.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.annotation.ActivityLogger;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.task.dto.request.TaskCreateRequest;
import org.devlogtwo.devlog.domain.task.dto.request.TaskStatusUpdateRequest;
import org.devlogtwo.devlog.domain.task.dto.request.TaskUpdateRequest;
import org.devlogtwo.devlog.domain.task.dto.response.TaskPageResponse;
import org.devlogtwo.devlog.domain.task.dto.response.TaskResponse;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.repository.TaskRepository;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskServiceApi {

    private final TaskRepository taskRepository;
    private final UserServiceApi userServiceApi;

    @ActivityLogger(type = ActivityType.TASK_CREATED)
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {

        // 담당자 ID 검증 로직
        User assignee = userServiceApi.findUserById(request.assigneeId());

        Task task = Task.create(request.title(), request.description(), request.priority(), assignee,
                request.dueDate());

        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    // 태스크 상세 조회
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long taskId) {

        Task task = findTaskById(taskId);

        return TaskResponse.from(task);
    }

    // 태스크 목록 조회
    @Transactional(readOnly = true)
    public TaskPageResponse getTaskList(TaskStatus status, Pageable pageable) {

        Page<Task> taskPage;

        if (status == null) {
            taskPage = taskRepository.findAll(pageable);
        } else {
            taskPage = taskRepository.findByStatus(status, pageable);
        }

        Page<TaskResponse> responsePage = taskPage.map(TaskResponse::from);

        return TaskPageResponse.from(responsePage);
    }

    // 태스크 상태 업데이트
    @ActivityLogger(type = ActivityType.TASK_STATUS_CHANGED)
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, TaskStatusUpdateRequest request) {

        Task task = findTaskById(taskId);

        task.updateStatus(request.status());

        return TaskResponse.from(task);
    }

    // 태스크 수정
    @ActivityLogger(type = ActivityType.TASK_UPDATED)
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {

        Task task = findTaskById(taskId);

        User assignee = userServiceApi.findUserById(request.assigneeId());

        task.update(request, assignee);

        return TaskResponse.from(task);
    }

    // 태스크 삭제
    @ActivityLogger(type = ActivityType.TASK_DELETED)
    @Transactional
    public void deleteTask(Long taskId) {

        Task task = findTaskById(taskId);

        taskRepository.delete(task);
    }

    // 태스크 작업 검색 페이징 API
    @Transactional(readOnly = true)
    public TaskPageResponse searchTask(String query, Pageable pageable) {

        Page<Task> taskPage = taskRepository.findByTitleContainsOrDescriptionContains(query, query, pageable);

        Page<TaskResponse> responsePage = taskPage.map(TaskResponse::from);

        return TaskPageResponse.from(responsePage);
    }


    @Override
    public Task findTaskById(Long taskId) {

        return taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.TASK_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> findAllByTitleContainsOrDescriptionContains(String title, String description) {

        return taskRepository.findAllByTitleContainsOrDescriptionContains(title, description);
    }
}
