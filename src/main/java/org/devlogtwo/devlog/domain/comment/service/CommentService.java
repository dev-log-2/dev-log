package org.devlogtwo.devlog.domain.comment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.annotation.ActivityLogger;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.domain.comment.dto.request.CommentCreateRequest;
import org.devlogtwo.devlog.domain.comment.dto.request.CommentUpdateRequest;
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


    @ActivityLogger(type = ActivityType.COMMENT_CREATED)
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, Long taskId, Long userId) {

        User user = userService.findUserById(userId);
        Task task = taskService.findTaskById(taskId);

        // 삼항연산자 어때? 리펙토링 해야겠지?
        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomBusinessException(ErrorCode.PARENT_COMMENT_NOT_FOUND));
            if (parent.getParent() != null) {
                throw new CustomBusinessException(ErrorCode.COMMENT_DEPTH_EXCEEDED);
            }
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
        //리팩토링 가능
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


    @ActivityLogger(type = ActivityType.COMMENT_DELETED)
    @Transactional
    public SuccessCode deleteComment(Long taskId, Long commentId, Long userid) {
        // 댓글조회 404에러
        Comment comment = findCommentById(commentId);

        // 권한확인
        if (!comment.getUser().getId().equals(userid)) {
            throw new CustomBusinessException(ErrorCode.COMMENT_NO_PERMISSION);
        }
        // 작성한 글의 댓글이 맞나요?
        if (!comment.getTask().getId().equals(taskId)) {
            throw new CustomBusinessException(ErrorCode.COMMENT_NOT_IN_TASK);
        }

        //재귀적삭제 메서드 호출
        int deletedCount = deleteCommentRecursively(comment);

        if (deletedCount > 1) {
            return SuccessCode.COMMENT_DELETED_WITH_REPLIES;
        } else {
            return SuccessCode.COMMENT_DELETED_SINGLE;
        }
    }

    //재귀적으로 댓글과 모든 대댓글 삭제
    private int deleteCommentRecursively(Comment comment) {
        // 자식 댓글 조회
        List<Comment> children = commentRepository.findByParentId(comment.getId());

        int count = 1; // 자기 자신 포함

        // 자식 댓글이 있으면 재귀적으로 삭제
        for (Comment child : children) {
            count += deleteCommentRecursively(child);
        }
        // 자기 자신 삭제
        commentRepository.delete(comment);

        return count;
    }

    //댓글수정
    @ActivityLogger(type = ActivityType.COMMENT_UPDATED)
    @Transactional
    public CommentResponse updateComment(
            Long userId,
            Long taskId,
            Long commentId,
            CommentUpdateRequest request
    ) {
        Comment comment = findCommentById(commentId);
        // 권한확인
        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomBusinessException(ErrorCode.COMMENT_NO_PERMISSION);
        }
        // 작성한 글의 댓글이 맞나요?
        if (!comment.getTask().getId().equals(taskId)) {
            throw new CustomBusinessException(ErrorCode.COMMENT_NOT_IN_TASK);
        }
        comment.updateContent(request.getContent());

        return CommentResponse.from(comment);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
