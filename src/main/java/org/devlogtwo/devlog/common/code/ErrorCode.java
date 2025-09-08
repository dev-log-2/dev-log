package org.devlogtwo.devlog.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // --- Common Errors ---
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. %s"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "입력이 잘못되었습니다. %s"),

    // --- User & Auth Errors ---
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자명입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "잘못된 사용자명 또는 비밀번호입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호 확인이 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다. 다시 로그인하세요"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 다시 로그인하세요."),

    // --- Comment Errors ---
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_NO_PERMISSION(HttpStatus.FORBIDDEN, "댓글을 삭제할 권한이 없습니다."),
    COMMENT_NOT_IN_TASK(HttpStatus.BAD_REQUEST, "해당 작업에 존재하지 않는 댓글입니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부모 댓글을 찾을 수 없습니다."),
    COMMENT_DEPTH_EXCEEDED(HttpStatus.FORBIDDEN, "대댓글에 댓글을 달 수 없습니다."),

    // --- Team & TeamMember Errors ---
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"),
    TEAM_MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 팀에 참여한 사용자입니다"),
    TEAM_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 팀명입니다"),

    // --- Task Errors ---
    INVALID_TASK_STATUS_CHANGE(HttpStatus.BAD_REQUEST, "상태는 TODO -> IN_PROGRESS -> DONE 순으로만 변경 가능합니다."),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 태스크를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(String... args) {
        return String.format(this.message, (Object[]) args);
    }
}


