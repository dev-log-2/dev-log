package org.devlogtwo.devlog.domain.dashboard.dto.response;

/**
 * @param totalTasks      전체 Task 개수
 * @param completedTasks  완료된 Task 개수
 * @param inProgressTasks 진행 중인 Task 개수
 * @param todoTasks       TODO 상태 Task 개수
 * @param overdueTasks    마감일 지난 미완료 Task 개수
 * @param teamProgress    팀 전체 진행률
 * @param myTasksToday    오늘 내가 처리해야 할 Task 개수
 * @Param completionRate  팀 완성률
 */

public record DashboardStatsResponse(long totalTasks, long completedTasks, long inProgressTasks, long todoTasks,
                                     long overdueTasks, long teamProgress, long myTasksToday, long completionRate) {
    // 정적 팩토리 메서드
    public static DashboardStatsResponse of(
            long totalTasks,
            long completedTasks,
            long inProgressTasks,
            long todoTasks,
            long overdueTasks,
            long teamProgress,
            long myTasksToday,
            long completionRate
    ) {
        return new DashboardStatsResponse(
                totalTasks,
                completedTasks,
                inProgressTasks,
                todoTasks,
                overdueTasks,
                teamProgress,
                myTasksToday,
                completionRate
        );
    }
}
