package org.devlogtwo.devlog.domain.comment.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record CommentPageResponse(
        List<CommentResponse> content,
        long totalElements,
        int totalPages,
        int size,
        int number
) {
    public static CommentPageResponse from(Page<CommentResponse> commentPage) { //
        return new CommentPageResponse(
                commentPage.getContent(),
                commentPage.getTotalElements(),
                commentPage.getTotalPages(),
                commentPage.getSize(),
                commentPage.getNumber()
        );
    }
}

