package org.devlogtwo.devlog.domain.comment.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.comment.dto.request.CommentCreateRequest;
import org.devlogtwo.devlog.domain.comment.dto.response.CommentCreateResponse;

import org.devlogtwo.devlog.domain.comment.entity.Comment;
import org.devlogtwo.devlog.domain.comment.repository.CommentRepository;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.service.TaskServiceApi;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentServiceApi {

    private final CommentRepository commentRepository;
    private final UserServiceApi userService;
    private final TaskServiceApi  taskService;

    @Transactional
    public CommentCreateResponse createComment(CommentCreateRequest request,Long taskId) {
        Long tempUserId =1L;

        User user = userService.findUserById(tempUserId);
        Task task = taskService.findTaskById(taskId);

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다. ID: " + request.getParentId()));
        }

        Comment newComment = Comment.create(
                user,
                task,
                request.getContent(),
                parent
        );
        Comment savedComment = commentRepository.save(newComment);

        return CommentCreateResponse.from(savedComment);
    }




}

