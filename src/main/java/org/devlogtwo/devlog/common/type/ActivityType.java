package org.devlogtwo.devlog.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityType {
    // 요구사항엔 있으나 명세서에는 없음
//    USER_LOGIN("사용자 로그인"),
//    USER_LOGOUT("사용자 로그아웃"),

    TASK_CREATE("작업 생성"),
    TASK_UPDATE("작업 수정"),
    TASK_DELETE("작업 삭제"),
    TASK_STATUS_CHANGE("작업 상태 변경"),
    COMMENT_CREATE("댓글 작성"),
    COMMENT_UPDATE("댓글 수정"),
    COMMENT_DELETE("댓글 삭제");

    private final String description;
}