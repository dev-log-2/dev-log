package org.devlogtwo.devlog.domain.actlog.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.devlogtwo.devlog.common.annotation.ActivityLogger;
import org.devlogtwo.devlog.common.security.UserPrincipal;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.actlog.service.ActivityLogService;
import org.devlogtwo.devlog.domain.task.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@SpringBootTest
class ActivityLogAspectTest {

    @Autowired
    private TestService testService;
    @MockBean
    private ActivityLogService activityLogService;
    @MockBean
    private TaskService taskService;
    @Autowired(required = false)
    private ActivityLogAspect activityLogAspect;

    private void authenticateAsUser(Long userId, String username) {
        UserPrincipal principal = new UserPrincipal(userId, username, UserRole.USER);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Aspect와 TestService Bean이 정상적으로 로드되는지 확인")
    void beansShouldBeLoaded() {
        assertThat(activityLogAspect).isNotNull();
        assertThat(testService).isNotNull();
        assertThat(testService.getClass().getName()).contains("CGLIB");
    }

    @Test
    @DisplayName("메서드 성공 시, 성공 로그가 저장된다")
    void whenMethodSucceeds_thenSuccessLogShouldBeSaved() {
        // given
        authenticateAsUser(1L, "testuser");
        ArgumentCaptor<ActivityType> typeCaptor = ArgumentCaptor.forClass(ActivityType.class);

        // when
        testService.successfulMethod("testParam", 123L);

        // then
        verify(activityLogService).saveLog(
                anyLong(), typeCaptor.capture(), anyString(), anyString(),
                anyBoolean(), anyString(), any(), any()
        );
        assertThat(typeCaptor.getValue()).isEqualTo(ActivityType.TASK_CREATED);
    }

    @Test
    @DisplayName("메서드 실패 시, 실패 로그가 예외 정보와 함께 저장된다")
    void whenMethodFails_thenFailureLogShouldBeSaved() {
        // given
        authenticateAsUser(2L, "errorUser");
        ArgumentCaptor<Boolean> successCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<ActivityType> typeCaptor = ArgumentCaptor.forClass(ActivityType.class);

        // when&then
        assertThatThrownBy(() -> testService.exceptionMethod())
                .isInstanceOf(IllegalArgumentException.class);

        verify(activityLogService).saveLog(
                anyLong(), typeCaptor.capture(), anyString(), anyString(),
                successCaptor.capture(), anyString(), any(), any()
        );
        assertThat(successCaptor.getValue()).isFalse();
        assertThat(typeCaptor.getValue()).isEqualTo(ActivityType.TASK_DELETED);
    }

    @Test
    @DisplayName("인증된 사용자가 없을 경우, 로그가 저장되지 않는다")
    void whenUserIsUnauthenticated_thenLogShouldNotBeSaved() {
        // 인증 설정 안 함
        testService.successfulMethod("param", 1L);

        verify(activityLogService, never()).saveLog(any(), any(), any(), any(), anyBoolean(), any(), any(), any());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    @Service
    public static class TestService {
        @ActivityLogger(type = ActivityType.TASK_CREATED)
        public String successfulMethod(String param1, Long param2) {
            return "Success Result";
        }

        @ActivityLogger(type = ActivityType.TASK_DELETED)
        public void exceptionMethod() {
            throw new IllegalArgumentException("Test Exception");
        }
    }

}