package org.devlogtwo.devlog.domain.comment.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
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


    @Transactional(readOnly = true)
    public CommentPageResponse getComments(Long taskId, Pageable pageable) {
        taskService.findTaskById(taskId);
        //부모 댓글 패이징해서 조회
        Page<Comment> parentPage = commentRepository.findByTaskIdAndParentIsNull(taskId, pageable);
        List<Comment> parents = parentPage.getContent();

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
        //부모와 자식을 합쳐서 반환하기 떄문에 정적매소드 팩토리를 안씀
        return new CommentPageResponse(
                finalCommentList,// 합치는 부분
                parentPage.getTotalElements(),
                parentPage.getTotalPages(),
                parentPage.getSize(),
                parentPage.getNumber()
        );
    }

    //나중에 ERROR 메세지 추가해야함
    @Transactional
    public SuccessCode deleteComment(Long id, Long taskId, Long commentId) {
        // 댓글조회 404에러
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        // 권한확인
        if (!comment.getUser().getId().equals(id)) {
            throw new IllegalArgumentException("댓글을 삭제할 권리가 업습니다.");
        }
        // 작성한 글의 댓글이 맞나요?
        if (!comment.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("해당 작업에 존재하지 않는 댓글입니다.");
        }
        //삭제후 개수를 받는다.
        int deletedCount = commentRepository.deleteCommentWithReplies(commentId);

        // 삭제된 개수에 따라 성공 메시지 반환
        if (deletedCount > 1) {
            return SuccessCode.COMMENT_DELETED_WITH_REPLIES;
        } else {
            return SuccessCode.COMMENT_DELETED_SINGLE;
        }
    }
}

