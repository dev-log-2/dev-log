package org.devlogtwo.devlog.domain.dashboard.dto.response;

import java.util.List;

public record MyTasksSummaryResponse(
        List<MyTasksResponse> todayTasks,
        List<MyTasksResponse> upcomingTasks,
        List<MyTasksResponse> overdueTasks
) {
    public static MyTasksSummaryResponse of(
            List<MyTasksResponse> todayTasks,
            List<MyTasksResponse> upcomingTasks,
            List<MyTasksResponse> overdueTasks
    ) {
        return new MyTasksSummaryResponse(todayTasks, upcomingTasks, overdueTasks);
    }
}
