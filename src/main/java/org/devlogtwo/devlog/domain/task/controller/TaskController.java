package org.devlogtwo.devlog.domain.task.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.task.dto.request.TaskCreateRequest;
import org.devlogtwo.devlog.domain.task.dto.response.TaskCreateResponse;
import org.devlogtwo.devlog.domain.task.dto.response.TaskResponse;
import org.devlogtwo.devlog.domain.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<GlobalApiResponse<TaskCreateResponse>> createTask(
            @Valid @RequestBody TaskCreateRequest request) {

        TaskCreateResponse response = taskService.createTask(request);

        return ResponseHelper.success(SuccessCode.TASK_CREATED, response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<GlobalApiResponse<TaskResponse>> getTask(@PathVariable Long taskId) {

        TaskResponse response = taskService.getTask(taskId);

        return ResponseHelper.success(SuccessCode.GET_TASK_SUCCESS, response);
    }
}
