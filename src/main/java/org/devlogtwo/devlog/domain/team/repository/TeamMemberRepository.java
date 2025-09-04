package org.devlogtwo.devlog.domain.team.repository;

import java.util.List;
import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<TeamMember> findByTeamId(Long teamId);

    @EntityGraph(attributePaths = {"user"})
    List<TeamMember> findByTeamIdIn(List<Long> teamIds);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    void deleteByTeamId(Long teamId);

    void deleteByTeamIdAndUserId(Long teamId, Long userId);
}
