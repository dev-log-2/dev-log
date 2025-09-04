package org.devlogtwo.devlog.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // --- Common Success ---

    // --- User Success ---
    SIGNUP_SUCCESS(HttpStatus.OK, "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인이 완료되었습니다."),
    GET_USER_INFO_SUCCESS(HttpStatus.OK, "사용자 정보를 조회했습니다."),
    WITHDRAWAL_SUCCESS(HttpStatus.OK, "회원탈퇴가 완료되었습니다."),

    // --- Task Success ---
    TASK_CREATED(HttpStatus.CREATED, "Task가 생성되었습니다."),
    GET_TASK_SUCCESS(HttpStatus.OK, "Task를 조회했습니다."),
    GET_TASKS_SUCCESS(HttpStatus.OK, "Task 목록을 조회했습니다."),
    TASK_STATUS_UPDATED(HttpStatus.OK, "작업 상태가 업데이트되었습니다."),

    //--- comment Success ---
    COMMENT_CREATED(HttpStatus.CREATED, "Comment가 생성되었습니다."),

    // --- Team Success ---
    TEAM_CREATE_SUCCESS(HttpStatus.CREATED, "팀이 성공적으로 생성되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
