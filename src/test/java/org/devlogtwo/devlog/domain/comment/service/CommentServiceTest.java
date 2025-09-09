package org.devlogtwo.devlog.domain.comment.service;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.TaskPriority;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.comment.dto.request.CommentCreateRequest;
import org.devlogtwo.devlog.domain.comment.entity.Comment;
import org.devlogtwo.devlog.domain.comment.repository.CommentRepository;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.repository.TaskRepository;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;


    @Test
    @DisplayName("Comment 생성 성공 테스트")
    void createComment_parentNull_success() {
        // 테스트용 User, Task 생성 (setUp 없이 메서드 안에서 직접)
        User user = User.signUp("testuser", "테스트유저", "test@test.com", "password", UserRole.USER);
        Task task = Task.create(
                "테스트 작업",
                "테스트 내용",
                TaskPriority.MEDIUM,
                user,
                LocalDateTime.now().plusDays(1)
        );

        // CommentCreateRequest 생성
        CommentCreateRequest request = new CommentCreateRequest("테스트 댓글", null);

        // 만약 create(User user, Task task, String content, Comment parent) 라면
        Comment comment = Comment.create(
                user,
                task,
                request.content(),
                null
        );

        // 검증 (예: 내용이 올바르게 들어갔는지)
        assert comment.getContent().equals("테스트 댓글");
        assert comment.getUser().equals(user);
        assert comment.getTask().equals(task);
        assert comment.getParent() == null;

    }


}
