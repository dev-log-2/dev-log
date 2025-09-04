package org.devlogtwo.devlog.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.comment.dto.request.CommentCreateRequest;
import org.devlogtwo.devlog.domain.comment.dto.response.CommentCreateResponse;
import org.devlogtwo.devlog.domain.comment.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task") // URL 경로를 단순화
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/{taskId}/comment")
    public ResponseEntity<GlobalApiResponse<CommentCreateResponse>> createCommentForTest(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentCreateRequest request
    ) {

        CommentCreateResponse response = commentService.createComment(request, taskId);

        return ResponseHelper.success(SuccessCode.COMMENT_CREATED, response);
    }
}