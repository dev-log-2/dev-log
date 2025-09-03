package org.devlogtwo.devlog.domain.team.repository;

import org.devlogtwo.devlog.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
