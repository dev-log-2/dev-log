package org.devlogtwo.devlog.domain.team.service;

import java.util.List;
import org.devlogtwo.devlog.domain.team.entity.Team;

public interface TeamServiceApi {

    Team findById(Long teamId);

    List<Team> findAll();

    boolean existsById(Long teamId);

    void delete(Team team);
}
