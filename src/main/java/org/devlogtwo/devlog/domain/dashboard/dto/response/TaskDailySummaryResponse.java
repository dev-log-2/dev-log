package org.devlogtwo.devlog.domain.dashboard.dto.response;

import java.time.LocalDate;

public record TaskDailySummaryResponse(
        LocalDate date,
        long totalTasks,
        long completedTasks
) {
    public static TaskDailySummaryResponse of(LocalDate date, long totalTasks, long completedTasks) {
        return new TaskDailySummaryResponse(
                date,
                totalTasks,
                completedTasks
        );
    }
}
