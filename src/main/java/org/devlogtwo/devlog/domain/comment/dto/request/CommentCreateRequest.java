package org.devlogtwo.devlog.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(

        @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
        @Size(max = 1000, message = "댓글은 최대 1000자까지 입력 가능합니다.")
        String content,

        @Positive(message = "부모 댓글 ID는 유효한 양수여야 합니다.")
        Long parentId // null 허용, null이 아닐 때만 @Positive 동작
) {
}
