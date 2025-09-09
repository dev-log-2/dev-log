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
@DisplayName("ActivityLogRepository 쿼리 메소드 테스트")
class ActivityLogRepositoryTest {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userA = User.signUp("userA", "User A", "userA@example.com", "Abcd1234!", UserRole.USER);
        userB = User.signUp("userB", "User B", "userB@example.com", "Abcd1234!", UserRole.USER);
        entityManager.persist(userA);
        entityManager.persist(userB);

        // userA 로그 3건 (시간 순차 확보 위해 짧은 sleep)
        persistLog(userA, ActivityType.TASK_CREATED, "createTask", "params1", true, "result1", 101L, null);
        sleep(20);
        persistLog(userA, ActivityType.TASK_UPDATED, "updateTask", "params2", true, "result2", 101L, null);
        sleep(20);
        persistLog(userA, ActivityType.TASK_DELETED, "deleteTask", "params3", true, "result3", 101L, null);

        // userB 로그 1건 (필터링 검증용)
        persistLog(userB, ActivityType.USER_LOGIN, "login", "loginParams", true, "loginResult", null, null);

        entityManager.flush();
        entityManager.clear();
    }

    private void persistLog(User user, ActivityType type, String method, String params, boolean success, String result,
                            Long taskId, Long commentId) {
        ActivityLog log = ActivityLog.create(user, type, method, params, success, result, taskId, commentId);
        entityManager.persist(log);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("findAllByUserIdOrderByCreatedAtDesc - 특정 사용자 로그를 생성일 내림차순으로 반환한다")
    void findAllByUserIdOrderByCreatedAtDesc_ReturnsDescendingLogs() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ActivityLog> page = activityLogRepository.findAllByUserIdOrderByCreatedAtDesc(userA.getId(), pageable);
        List<ActivityLog> content = page.getContent();

        assertThat(content).hasSize(3);
        // 최신(마지막으로 persist)한 deleteTask 가 첫 번째
        assertThat(content.get(0).getMethodName()).isEqualTo("deleteTask");
        assertThat(content.get(1).getMethodName()).isEqualTo("updateTask");
        assertThat(content.get(2).getMethodName()).isEqualTo("createTask");

        // createdAt 순서 검증
        assertThat(content.get(0).getCreatedAt()).isAfter(content.get(1).getCreatedAt());
        assertThat(content.get(1).getCreatedAt()).isAfter(content.get(2).getCreatedAt());

        // 다른 사용자 로그가 포함되지 않았는지
        assertThat(content).allMatch(log -> log.getUser().getId().equals(userA.getId()));
    }

    @Test
    @DisplayName("findAllByUserIdOrderByCreatedAtDesc - 페이징 동작을 검증한다")
    void findAllByUserIdOrderByCreatedAtDesc_Pagination() {
        Pageable firstPageReq = PageRequest.of(0, 2);
        Pageable secondPageReq = PageRequest.of(1, 2);

        Page<ActivityLog> firstPage = activityLogRepository.findAllByUserIdOrderByCreatedAtDesc(userA.getId(),
                firstPageReq);
        Page<ActivityLog> secondPage = activityLogRepository.findAllByUserIdOrderByCreatedAtDesc(userA.getId(),
                secondPageReq);

        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(secondPage.getContent()).hasSize(1);

        // 첫 페이지 최신 2건
        assertThat(firstPage.getContent().get(0).getMethodName()).isEqualTo("deleteTask");
        assertThat(firstPage.getContent().get(1).getMethodName()).isEqualTo("updateTask");
        // 두 번째 페이지 남은 1건
        assertThat(secondPage.getContent().get(0).getMethodName()).isEqualTo("createTask");
    }
}

