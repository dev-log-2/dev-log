package org.devlogtwo.devlog.domain.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Optional;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.type.TaskPriority;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.task.dto.request.TaskCreateRequest;
import org.devlogtwo.devlog.domain.task.dto.response.TaskResponse;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.repository.TaskRepository;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserServiceApi userServiceApi;

    @InjectMocks
    private TaskService taskService;

    private User assignee;
    private Task task;

    @BeforeEach
    void setUp() {
        assignee = User.signUp("testuser", "테스트유저", "test@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(assignee, "id", 1L);

        task = Task.create("새로운 태스크", "설명", TaskPriority.MEDIUM, assignee, LocalDateTime.now().plusDays(5));
        ReflectionTestUtils.setField(task, "id", 1L);
    }

    @Test
    @DisplayName("Task 생성 성공 테스트")
    void createTask() {
        // Given
        TaskCreateRequest request = new TaskCreateRequest(
                "새로운 태스크", "설명", LocalDateTime.now().plusDays(1), TaskPriority.MEDIUM, 1L);

        given(userServiceApi.findUserById(request.assigneeId())).willReturn(assignee);
        given(taskRepository.save(any(Task.class))).willReturn(task);

        // when
        TaskResponse response = taskService.createTask(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo(task.getTitle());
        assertThat(response.assignee().id()).isEqualTo(assignee.getId());
    }

    @Test
    @DisplayName("태스크 상세 조회 성공 테스트")
    void getTasks() {
        // given
        Long taskId = 1L;
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        // when
        TaskResponse response = taskService.getTask(taskId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(taskId);
        assertThat(response.title()).isEqualTo(task.getTitle());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다.")
    void getTask_TaskNotFound() {
        // given
        Long taskId = 999L;
        given(taskRepository.findById(taskId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CustomBusinessException.class, () -> {
            taskService.getTask(taskId);
        });
    }


}
