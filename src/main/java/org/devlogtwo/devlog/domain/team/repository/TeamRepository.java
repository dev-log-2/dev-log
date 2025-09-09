package org.devlogtwo.devlog.domain.team.repository;

import java.util.List;
import org.devlogtwo.devlog.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByName(String name);

    List<Team> findAllByNameContainsOrDescriptionContains(String name, String description);
}
