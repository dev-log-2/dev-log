package org.devlogtwo.devlog.domain.comment.dto.response;

import java.util.List;

public record CommentPageResponse(
        List<CommentResponse> content,
        long totalElements,
        int totalPages,
        int size,
        int number
) {

}

