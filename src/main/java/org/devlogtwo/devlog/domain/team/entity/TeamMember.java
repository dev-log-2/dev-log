package org.devlogtwo.devlog.domain.team.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devlogtwo.devlog.common.type.TeamRole;
import org.devlogtwo.devlog.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Team team;

    @Column(nullable = false)
    private TeamRole teamRole;

    @Builder
    private TeamMember(Long id, User user, Team team, TeamRole teamRole) {
        this.id = id;
        this.user = user;
        this.team = team;
        this.teamRole = teamRole;
    }

    public static TeamMember of(Long id, User user, Team team, TeamRole teamRole) {
        return TeamMember.builder().id(id).user(user).team(team).teamRole(teamRole).build();
    }
}
