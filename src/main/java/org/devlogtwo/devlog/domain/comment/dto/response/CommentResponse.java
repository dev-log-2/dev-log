package org.devlogtwo.devlog.domain.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import org.devlogtwo.devlog.domain.comment.entity.Comment;


public record CommentResponse(
        Long id,
        String content,
        Long taskId,
        Long userId,
        CommentUserResponse user,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long parentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CommentResponse from(Comment comment) {
        // 부모 ID는 null일 수 있으므로 null 체크
        Long parentId = (comment.getParent() != null) ? comment.getParent().getId() : null;

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getTask().getId(),
                comment.getUser().getId(),
                CommentUserResponse.from(comment.getUser()),
                parentId,
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
