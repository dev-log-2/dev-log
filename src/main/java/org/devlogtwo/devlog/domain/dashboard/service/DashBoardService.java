package org.devlogtwo.devlog.domain.dashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.dto.PageResponse;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.common.util.DescriptionGenerator;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.devlogtwo.devlog.domain.actlog.service.ActivityLogServiceApi;
import org.devlogtwo.devlog.domain.dashboard.dto.response.DashboardRecentActivityResponse;
import org.devlogtwo.devlog.domain.dashboard.dto.response.DashboardStatsResponse;
import org.devlogtwo.devlog.domain.dashboard.dto.response.MyTasksResponse;
import org.devlogtwo.devlog.domain.dashboard.dto.response.MyTasksSummaryResponse;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.service.TaskServiceApi;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.devlogtwo.devlog.domain.team.service.TeamMemberServiceApi;
import org.devlogtwo.devlog.domain.team.service.TeamServiceApi;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashBoardService {
    private final TaskServiceApi taskServiceApi;
    private final TeamMemberServiceApi teamMemberService;
    private final TeamServiceApi teamService;
    private final ActivityLogServiceApi activityLogServiceApi;
    private final DescriptionGenerator descriptionGenerator;

    //대시보드 통계 조회
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

        // 진행률 = (완료된 작업+진행중 작업*0.3 )/전체 작업 * 100
        long teamProgress = (totalTasks > 0) ? (long) ((completedTasks + inProgressTasks * 0.3) * 100 / totalTasks) : 0;

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

    public MyTasksSummaryResponse getMyTasks(Long userId) {
        LocalDate Today = LocalDate.now();

        List<Task> allTasksByAssignee = taskServiceApi.findAllByAssignee_Id(userId);
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


    public Map<String, Integer> getTeamProgress() {

        List<TeamMember> teamMembers = teamMemberService.findAll();

        Map<Long, String> teamNames = teamService.findAll().stream()
                .collect(Collectors.toMap(
                        Team::getId,
                        Team::getName
                ));

        Map<Long, List<TeamMember>> membersByTeamId = teamMembers.stream()
                .collect(Collectors.groupingBy(
                        teamMember -> teamMember.getTeam().getId()
                ));

        Map<String, Integer> progressByTeam = new HashMap<>();

        membersByTeamId.forEach((teamId, members) -> {
            long totalTasks = 0;
            long doneTasks = 0;

            for (TeamMember teamMember : members) {
                User user = teamMember.getUser();
                long allCount = taskServiceApi.countByAssignee(user);
                totalTasks += allCount;
                long doneCount = taskServiceApi.countByAssigneeAndStatus(user, TaskStatus.DONE);
                doneTasks += doneCount;
            }
            int progress = totalTasks == 0 ? 0 : (int) ((doneTasks * 100) / totalTasks);
            progressByTeam.put(teamNames.get(teamId), progress);
        });

        return progressByTeam;
    }


    public PageResponse<DashboardRecentActivityResponse> getRecentActivity(Pageable pageable) {

        Page<ActivityLog> activityLogPage = activityLogServiceApi.findAllByOrderByCreatedAtDesc(pageable);

        Page<DashboardRecentActivityResponse> responsePage = activityLogPage
                .map(activityLog -> DashboardRecentActivityResponse.of(activityLog,
                        descriptionGenerator.createDescription(activityLog)));

        return PageResponse.from(responsePage);
    }
}
