package org.devlogtwo.devlog.domain.dashboard.dto.response;

import java.time.LocalDate;

public record WeeklyTrendResponse(
        String name,      // 요일
        long tasks,       // 해당 요일의 총 작업 수 (마감일 기준)
        long completed,   // 해당 요일의 완료된 작업 수 (마감일 기준)
        LocalDate date
) {
    public static WeeklyTrendResponse of(String dayName, TaskDailySummaryResponse summary, LocalDate date) {
        return new WeeklyTrendResponse(
                dayName,
                summary.totalTasks(),
                summary.completedTasks(),
                date
        );
    }
}
