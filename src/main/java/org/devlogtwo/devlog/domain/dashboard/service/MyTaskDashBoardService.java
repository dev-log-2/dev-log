package org.devlogtwo.devlog.domain.dashboard.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.dashboard.dto.response.MyTasksResponse;
import org.devlogtwo.devlog.domain.dashboard.dto.response.MyTasksSummaryResponse;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.service.TaskService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyTaskDashBoardService {

    private final TaskService taskService;

    public MyTasksSummaryResponse getMyTasks(Long userId) {
        LocalDate Today = LocalDate.now();

        List<Task> allTasksByAssignee = taskService.findAllByAssignee_Id(userId);
        List<MyTasksResponse> todayTasks = allTasksByAssignee.stream()
                .filter(task -> task.getDueDate().toLocalDate().equals(Today))
                .map(MyTasksResponse::from).toList();
        List<MyTasksResponse> upcomingTasks = allTasksByAssignee.stream()
                .filter(task -> task.getDueDate().toLocalDate().isAfter(Today))
                .map(MyTasksResponse::from).toList();
        List<MyTasksResponse> overdueTasks = allTasksByAssignee.stream()
                .filter(task -> task.getDueDate().toLocalDate().isBefore(Today))
                .map(MyTasksResponse::from).toList();

        return MyTasksSummaryResponse.of(todayTasks, upcomingTasks, overdueTasks);
    }
}