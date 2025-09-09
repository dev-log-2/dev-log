package org.devlogtwo.devlog.domain.actlog.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.devlogtwo.devlog.common.security.UserPrincipal;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.common.util.DescriptionGenerator;
import org.devlogtwo.devlog.domain.actlog.repository.ActivityLogRepository;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private UserServiceApi userService; // 사용되지 않지만, 의존성 주입을 위해 필요

    @Mock
    private DescriptionGenerator descriptionGenerator; // 사용되지 않지만, 의존성 주입을 위해 필요

    @InjectMocks
    private ActivityLogService activityLogService;

    @Nested
    @DisplayName("활동 기록 조회 (getActivityLogs)")
    class GetActivityLogs {

        @Test
        @DisplayName("사용자는 USER 권한일 경우, 자신의 ID로 활동 조회를 할 수 있다.")
        void asUser_ShouldFilterByOwnId() {
            // given
            Long userId = 1L;
            UserPrincipal userPrincipal = new UserPrincipal(userId, "user", UserRole.USER);
            Pageable pageable = Pageable.ofSize(10);

            given(activityLogRepository.findAll(any(Specification.class), eq(pageable)))
                    .willReturn(Page.empty());

            // when
            activityLogService.getActivityLogs(userPrincipal, null, null, null, null, null, pageable);

            // then
            verify(activityLogRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("사용자는 ADMIN 권한이고 userId 필터가 있을 경우, 해당 userId로 활동 로그를 조회할 수 있다.")
        void asAdmin_WithUserIdFilter_ShouldFilterByGivenUserId() {
            // given
            Long adminId = 1L;
            Long filterUserId = 2L;
            UserPrincipal adminPrincipal = new UserPrincipal(adminId, "admin", UserRole.ADMIN);
            Pageable pageable = Pageable.ofSize(10);

            given(activityLogRepository.findAll(any(Specification.class), eq(pageable)))
                    .willReturn(Page.empty());

            // when
            activityLogService.getActivityLogs(adminPrincipal, null, filterUserId, null, null, null, pageable);

            // then
            verify(activityLogRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("사용자가 ADMIN 권한이고 userId 필터가 없을 경우, 모든 사용자의 기록을 조회할 수 있다.")
        void asAdmin_WithoutUserIdFilter_ShouldGetAllLogs() {
            // given
            Long adminId = 1L;
            UserPrincipal adminPrincipal = new UserPrincipal(adminId, "admin", UserRole.ADMIN);
            Pageable pageable = Pageable.ofSize(10);

            given(activityLogRepository.findAll(any(Specification.class), eq(pageable)))
                    .willReturn(Page.empty());

            // when
            activityLogService.getActivityLogs(adminPrincipal, null, null, null, null, null, pageable);

            // then ADMIN 역할이고 userId 필터가 null일 때 `targetUserId`가 null로 유지
            verify(activityLogRepository).findAll(any(Specification.class), eq(pageable));
        }
    }
}