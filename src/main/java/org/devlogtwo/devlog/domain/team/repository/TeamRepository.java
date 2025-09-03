package org.devlogtwo.devlog.domain.team.repository;

import org.devlogtwo.devlog.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
