package org.devlogtwo.devlog.domain.actlog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.devlogtwo.devlog.common.config.JpaAuditingConfig;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ActivityLogRepositoryTest {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.signUp(
                "testuser",
                "Test User",
                "test@example.com",
                "1234qwerAS!",
                UserRole.USER
        );
        entityManager.persist(testUser);

        ActivityLog log1 = ActivityLog.create(testUser, ActivityType.TASK_CREATED, 1L, null, "첫 번째 로그");
        entityManager.persist(log1);

        entityManager.flush();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ActivityLog log2 = ActivityLog.create(testUser, ActivityType.TASK_UPDATED, 1L, null, "두 번째 로그");
        entityManager.persist(log2);
        entityManager.flush();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ActivityLog log3 = ActivityLog.create(testUser, ActivityType.TASK_DELETED, 1L, null, "세 번째 로그");
        entityManager.persist(log3);
        entityManager.flush();
    }

    @Test
    @DisplayName("findAllByOrderByCreatedAtDesc - 생성 시간 내림차순으로 모든 활동 로그를 페이지네이션하여 조회한다")
    void findAllByOrderByCreatedAtDesc_ShouldReturnLogsInDescendingOrderOfCreation() {

        // given
        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<ActivityLog> resultPage = activityLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<ActivityLog> logs = resultPage.getContent();
        for (ActivityLog log : logs) {
            System.out.println("log.getId() = " + log.getId());
            System.out.println("log.getDescription() = " + log.getDescription());
            System.out.println("log.getCreatedAt() = " + log.getCreatedAt());
        }

        // then
        assertThat(resultPage).isNotNull();
        assertThat(logs).hasSize(3);

        // 가장 나중에 생성된 '세 번째 로그'가 첫 번째로 와야 함
        assertThat(logs.get(0).getDescription()).isEqualTo("세 번째 로그");
        assertThat(logs.get(1).getDescription()).isEqualTo("두 번째 로그");
        assertThat(logs.get(2).getDescription()).isEqualTo("첫 번째 로그");

        // 더 확실한 검증: createdAt 타임스탬프를 직접 비교
        assertThat(logs.get(0).getCreatedAt()).isAfter(logs.get(1).getCreatedAt());
        assertThat(logs.get(1).getCreatedAt()).isAfter(logs.get(2).getCreatedAt());
    }
}