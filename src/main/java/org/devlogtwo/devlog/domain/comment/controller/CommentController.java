package org.devlogtwo.devlog.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.comment.dto.request.CommentCreateRequest;
import org.devlogtwo.devlog.domain.comment.dto.response.CommentPageResponse;
import org.devlogtwo.devlog.domain.comment.dto.response.CommentResponse;
import org.devlogtwo.devlog.domain.comment.service.CommentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task/{taskId}") // URL 경로를 단순화
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/comment")
    public ResponseEntity<GlobalApiResponse<CommentResponse>> createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentCreateRequest request
    ) {

        CommentResponse response = commentService.createComment(request, taskId);

        return ResponseHelper.success(SuccessCode.COMMENT_CREATED, response);
    }

    @GetMapping("/comment")
    public ResponseEntity<GlobalApiResponse<CommentPageResponse>> getCommentList(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,// 첫번째 페이지
            @RequestParam(defaultValue = "10") int size, // 10개씩!
            @RequestParam(defaultValue = "newest") String sort //최신순 디폴트
    ) {

        Sort sortOrder = sort.equalsIgnoreCase("oldest") ?
                Sort.by("createdAt").ascending() :
                Sort.by("createdAt").descending();// 기본값 newest

        // 쿼리로 받은 정보를 담는다?
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        CommentPageResponse response = commentService.getComments(taskId, pageable);

        return ResponseHelper.success(SuccessCode.COMMENT_LIST_VIEWED, response);
    }

}