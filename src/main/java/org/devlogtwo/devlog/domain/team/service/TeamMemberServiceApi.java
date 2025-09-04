package org.devlogtwo.devlog.domain.team.service;

import java.util.List;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;

public interface TeamMemberServiceApi {

    List<TeamMemberResponse> findTeamMembers(Long teamId);

    List<TeamMember> findByTeamIds(List<Long> teamIds);
}
