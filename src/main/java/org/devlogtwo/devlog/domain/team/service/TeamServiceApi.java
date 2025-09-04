package org.devlogtwo.devlog.domain.team.service;

import org.devlogtwo.devlog.domain.team.entity.Team;

public interface TeamServiceApi {

    Team findById(Long teamId);
}
