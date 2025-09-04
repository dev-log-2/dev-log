package org.devlogtwo.devlog.domain.user.repository;

import java.util.List;
import java.util.Optional;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query(value = """
            SELECT u 
            FROM User u
            WHERE u.deletedAt IS NULL AND u.id NOT IN 
                        (SELECT tm.user.id FROM TeamMember tm WHERE tm.team.id = :teamId)
            """)
    List<User> findAvailableUsersForTeam(@Param("teamId") Long teamId);

    @Query(value = """
            SELECT COUNT(*)
            FROM user 
            WHERE username = :username""",
            nativeQuery = true)
    Integer existsByUsernameIgnoringSoftDelete(@Param("username") String username);

    // ✅ nativeQuery = true를 사용하여 @SQLRestriction을 우회합니다.
    @Query(value = """
            SELECT COUNT(*)
            FROM user 
            WHERE email = :email""",
            nativeQuery = true)
    Integer existsByEmailIgnoringSoftDelete(@Param("email") String email);
}

