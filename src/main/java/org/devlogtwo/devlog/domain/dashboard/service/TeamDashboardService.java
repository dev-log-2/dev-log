package org.devlogtwo.devlog.domain.dashboard.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.task.service.TaskServiceApi;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.devlogtwo.devlog.domain.team.service.TeamMemberServiceApi;
import org.devlogtwo.devlog.domain.team.service.TeamServiceApi;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamDashboardService {

    private final TeamServiceApi teamService;
    private final TeamMemberServiceApi teamMemberService;
    private final TaskServiceApi taskService;


    public Map<String, Integer> getTeamProgress() {

        List<TeamMember> teamMembers = teamMemberService.findAll();

        Map<Long, String> teamNames = teamService.findAll().stream()
                .collect(Collectors.toMap(
                        Team::getId,
                        Team::getName
                ));

        Map<Long, List<TeamMember>> membersByTeamId = teamMembers.stream()
                .collect(Collectors.groupingBy(
                        teamMember -> teamMember.getTeam().getId()    // 팀 ID로 그룹핑
                ));

        Map<String, Integer> progressByTeam = new HashMap<>();

        membersByTeamId.forEach((teamId, members) -> {
            long totalTasks = 0;
            long doneTasks = 0;

            for (TeamMember teamMember : members) {
                User user = teamMember.getUser();
                long allCount = taskService.countByAssignee(user);
                totalTasks += allCount;
                long doneCount = taskService.countByAssigneeAndStatus(user, TaskStatus.DONE);
                doneTasks += doneCount;
            }
            int progress = totalTasks == 0 ? 0 : (int) ((doneTasks * 100) / totalTasks);
            progressByTeam.put(teamNames.get(teamId), progress);
        });

        return progressByTeam;
    }
}
