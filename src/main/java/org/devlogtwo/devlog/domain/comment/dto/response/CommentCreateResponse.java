package org.devlogtwo.devlog.domain.comment.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.domain.comment.entity.Comment;


public record CommentCreateResponse(Long commentId, String content, Long userId, Long parentId, Long taskId,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static CommentCreateResponse from(Comment comment) {
        return new CommentCreateResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getParent().getId(),
                comment.getTask().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()

                );
    }
}
