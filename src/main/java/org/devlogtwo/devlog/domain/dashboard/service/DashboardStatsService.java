package org.devlogtwo.devlog.domain.dashboard.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.dashboard.dto.response.DashboardStatsResponse;
import org.devlogtwo.devlog.domain.task.service.TaskServiceApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardStatsService {
    private final TaskServiceApi taskServiceApi;

    //대시보드 통계 조회
    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats(Long id) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1).minusNanos(1);
        //전체 task 조회
        long totalTasks = taskServiceApi.count();
        //task staus별  작업들의 개수
        long completedTasks = taskServiceApi.countByStatus(TaskStatus.DONE);
        long inProgressTasks = taskServiceApi.countByStatus(TaskStatus.IN_PROGRESS);
        long todoTasks = taskServiceApi.countByStatus(TaskStatus.TODO);
        //팀 별 기한이 초과된 작업들의 개수(마감일이 지났는데 done 상태가 안된)
        long overdueTasks = taskServiceApi.countByDueDateBeforeAndStatusNot(now, TaskStatus.DONE);
        //오늘 내가 처리해야 할 작업 수
        long myTasksToday = taskServiceApi.countByAssigneeIdAndDueDateBetween(id, todayStart, todayEnd);

        //완료율(팀에 소속된 사림들이 완료한 작업 수/ 팀에 소속된 사람들이 맡은 전체 작업 수)*100
        long completionRate = (totalTasks > 0) ? (completedTasks * 100 / totalTasks) : 0;

        // 팀 전체 진행률(완료된 작업수/ 전체 작업 수)*100
        long teamProgress = completionRate;

        return DashboardStatsResponse.of(
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