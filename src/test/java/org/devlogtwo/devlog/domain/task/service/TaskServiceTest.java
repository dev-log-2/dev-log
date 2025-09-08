package org.devlogtwo.devlog.domain.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.Optional;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.type.TaskPriority;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.task.dto.request.TaskCreateRequest;
import org.devlogtwo.devlog.domain.task.dto.request.TaskStatusUpdateRequest;
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
    @DisplayName("담당자가 존재 하지 않을 시 태스크 생성 시 예외가 발생한다.")
    public void createTask_UserNotFound() {
        // given
        Long userId = 999L;
        TaskCreateRequest request = new TaskCreateRequest(
                "새로운 태스크", "설명", LocalDateTime.now().plusDays(1), TaskPriority.MEDIUM, userId);

        given(userServiceApi.findUserById(request.assigneeId())).willThrow(
                new CustomBusinessException(ErrorCode.USER_NOT_FOUND));

        // when & then
        assertThrows(CustomBusinessException.class, () -> taskService.createTask(request));
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

    @Test
    @DisplayName("태스크 상태 변경 성공 테스트")
    void updateTaskStatus() {
        // given
        Long taskId = 1L;
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.IN_PROGRESS);

        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        // when
        TaskResponse response = taskService.updateTaskStatus(taskId, request);

        // then
        assertThat(response.status()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("잘못된 태스크 상태 변경 시 예외가 발생한다.")
    void updateTaskStatus_InvalidChange() {
        // given
        Long taskId = 1L;
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.DONE);
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        // when & then
        CustomBusinessException exception = assertThrows(CustomBusinessException.class, () -> {
            taskService.updateTaskStatus(taskId, request);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TASK_STATUS_CHANGE);
    }

    @Test
    @DisplayName("태스크 삭제 성공")
    void deleteTask() {
        // given
        Long taskId = 1L;
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        willDoNothing().given(taskRepository).delete(task);

        // when
        taskService.deleteTask(taskId);

        // then
        then(taskRepository).should(times(1)).delete(task);
    }


}
