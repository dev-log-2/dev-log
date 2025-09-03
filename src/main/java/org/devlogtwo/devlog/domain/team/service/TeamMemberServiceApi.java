package org.devlogtwo.devlog.domain.team.service;

import java.util.List;
import org.devlogtwo.devlog.domain.team.dto.response.TeamMemberResponse;

public interface TeamMemberServiceApi {

    List<TeamMemberResponse> findTeamMembers(Long teamId);
}
