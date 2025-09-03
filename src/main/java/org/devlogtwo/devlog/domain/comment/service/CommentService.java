package org.devlogtwo.devlog.domain.comment.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.comment.dto.request.CommentCreateRequest;
import org.devlogtwo.devlog.domain.comment.dto.response.CommentCreateResponse;

import org.devlogtwo.devlog.domain.comment.entity.Comment;
import org.devlogtwo.devlog.domain.comment.repository.CommentRepository;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.repository.TaskRepository;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.repository.UserRepository;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentServiceApi {

    private final CommentRepository commentRepository;
    private final UserServiceApi userService;
    private final TaskRepository taskRepository;

    @Transactional
    public CommentCreateResponse createComment(CommentCreateRequest request) {

        Long tempUserId = 1L;
        Long tempTaskId = 1L;


        User user = userService.findUserById(tempUserId);

        Task task = taskRepository.findById(tempTaskId)
                .orElseThrow(() -> new EntityNotFoundException("테스트로 작성된 작업을 찾을수없습니다."));

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

