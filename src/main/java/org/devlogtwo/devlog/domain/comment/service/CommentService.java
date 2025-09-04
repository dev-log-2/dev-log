package org.devlogtwo.devlog.domain.comment.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.domain.comment.dto.request.CommentCreateRequest;
import org.devlogtwo.devlog.domain.comment.dto.response.CommentPageResponse;
import org.devlogtwo.devlog.domain.comment.dto.response.CommentResponse;
import org.devlogtwo.devlog.domain.comment.entity.Comment;
import org.devlogtwo.devlog.domain.comment.repository.CommentRepository;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.service.TaskServiceApi;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentServiceApi {

    private final CommentRepository commentRepository;
    private final UserServiceApi userService;
    private final TaskServiceApi taskService;

    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, Long taskId) {
        //t 유저 받아오면 봐꿔야됨
        Long tempUserId = 1L;

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

        return CommentResponse.from(savedComment);
    }

    @Transactional
    public CommentPageResponse getComments(Long taskId, Pageable pageable) {
        taskService.findTaskById(taskId);
        //부모 댓글 패이징해서 조회
        Page<Comment> parentPage = commentRepository.findByTaskIdAndParentIsNull(taskId, pageable);
        List<Comment> parents = parentPage.getContent();

        if (parents.isEmpty()) {
            return CommentPageResponse.from(Page.empty()); // 부모 댓글이 없으면 빈 페이지 반환
        }
        //자식 댓글들을 쿼리를 쏴서 조회
        List<Comment> children = commentRepository.findByParentInOrderByCreatedAtAsc(parents);

        // 3. 자식 댓글들을 부모 ID별로 그룹화
        Map<Long, List<Comment>> childrenMap = children.stream()
                .collect(Collectors.groupingBy(comment -> comment.getParent().getId()));

        // 4. 응답 리스트 부모에서 자식 순서로
        List<CommentResponse> finalCommentList = new ArrayList<>();
        for (Comment parent : parents) {
            // 부모 댓글 DTO로 변환 후 추가
            finalCommentList.add(CommentResponse.from(parent));
            // 해당 부모의 자식 댓글들 -> dto로 변환
            List<Comment> replies = childrenMap.getOrDefault(parent.getId(), List.of());
            replies.forEach(reply -> finalCommentList.add(CommentResponse.from(reply)));
        }

        //출력
        return new CommentPageResponse(
                finalCommentList,
                parentPage.getTotalElements(),
                parentPage.getTotalPages(),
                parentPage.getSize(),
                parentPage.getNumber()
        );
    }
}

