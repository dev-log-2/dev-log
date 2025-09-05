package org.devlogtwo.devlog.domain.task.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.task.dto.request.TaskCreateRequest;
import org.devlogtwo.devlog.domain.task.dto.request.TaskStatusUpdateRequest;
import org.devlogtwo.devlog.domain.task.dto.request.TaskUpdateRequest;
import org.devlogtwo.devlog.domain.task.dto.response.TaskPageResponse;
import org.devlogtwo.devlog.domain.task.dto.response.TaskResponse;
import org.devlogtwo.devlog.domain.task.service.TaskService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<GlobalApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskCreateRequest request) {

        TaskResponse response = taskService.createTask(request);

        return ResponseHelper.success(SuccessCode.TASK_CREATED, response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<GlobalApiResponse<TaskResponse>> getTask(@PathVariable Long taskId) {

        TaskResponse response = taskService.getTask(taskId);

        return ResponseHelper.success(SuccessCode.GET_TASK_SUCCESS, response);
    }

    @GetMapping
    public ResponseEntity<GlobalApiResponse<TaskPageResponse>> getTaskList(
            @RequestParam(required = false) TaskStatus status, Pageable pageable) {

        TaskPageResponse response = taskService.getTaskList(status, pageable);

        return ResponseHelper.success(SuccessCode.GET_TASKS_SUCCESS, response);
    }

    @GetMapping("/search")
    public ResponseEntity<GlobalApiResponse<TaskPageResponse>> searchTask(@RequestParam("query") String query, @PageableDefault(size = 10)
                                                                          Pageable pageable) {

        TaskPageResponse response = taskService.searchTask(query, pageable);

        return ResponseHelper.success(SuccessCode.SEARCH_SUCCESS, response);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<GlobalApiResponse<TaskResponse>> updateTaskStatus(@PathVariable Long taskId,
                                                                            @Valid @RequestBody TaskStatusUpdateRequest request) {

        TaskResponse response = taskService.updateTaskStatus(taskId, request);

        return ResponseHelper.success(SuccessCode.TASK_STATUS_UPDATED, response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<GlobalApiResponse<TaskResponse>> updateTask(@PathVariable Long taskId,
                                                                      @Valid @RequestBody TaskUpdateRequest request) {

        TaskResponse response = taskService.updateTask(taskId, request);

        return ResponseHelper.success(SuccessCode.TASK_UPDATED, response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<GlobalApiResponse<Void>> deleteTask(@PathVariable Long taskId) {

        taskService.deleteTask(taskId);

        return ResponseHelper.success(SuccessCode.TASK_DELETED);
    }
}
