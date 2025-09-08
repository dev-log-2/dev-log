package org.devlogtwo.devlog.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // --- Common Success ---
    REQUEST_SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),

    // --- Activity Log Success --
    ACTIVITY_LOG_SUCCESS(HttpStatus.OK, "활동 로그를 조회했습니다."),

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
    TASK_UPDATED(HttpStatus.OK, "Task가 수정되었습니다."),
    TASK_DELETED(HttpStatus.OK, "태스크 삭제 성공"),

    //--- comment Success ---
    COMMENT_CREATED(HttpStatus.CREATED, "Comment가 생성되었습니다."),
    COMMENT_LIST_VIEWED(HttpStatus.OK, "댓글 목록을 조회했습니다."),
    COMMENT_DELETED_SINGLE(HttpStatus.OK, "댓글이 삭제되었습니다."),
    COMMENT_DELETED_WITH_REPLIES(HttpStatus.OK, "댓글과 대댓글들이 삭제되었습니다."),
    COMMENT_UPDATED(HttpStatus.OK, "댓글이 수정되었습니다."),

    // --- Team Success ---
    TEAM_CREATE_SUCCESS(HttpStatus.CREATED, "팀이 성공적으로 생성되었습니다."),
    TEAM_DETAIL_SUCCESS(HttpStatus.OK, "팀 정보를 조회했습니다."),
    TEAM_LIST_SUCCESS(HttpStatus.OK, "팀 목록이 성공적으로 조회되었습니다."),
    TEAM_MEMBER_JOIN_SUCCESS(HttpStatus.CREATED, "멤버가 성공적으로 추가되었습니다."),
    TEAM_DELETE_SUCCESS(HttpStatus.OK, "팀이 성공적으로 삭제되었습니다."),
    TEAM_MEMBER_DELETE_SUCCESS(HttpStatus.OK, "팀 멤버가 성공적으로 제거되었습니다."),
    TEAM_UPDATE_SUCCESS(HttpStatus.OK, "요청 성공"),
    TEAM_MEMBER_LIST_SUCCESS(HttpStatus.OK, "팀 멤버 목록을 조회했습니다."),
    TEAM_AVAILABLE_MEMBER_LIST_SUCCESS(HttpStatus.OK, "사용 가능한 사용자 목록을 조회했습니다."),

    // --- Dashboard Success ---
    DASHBOARD_TEAM_PROGRESS_SUCCESS(HttpStatus.OK, "팀 진행률 조회 완료"),
    DASHBOARD_RECENT_ACTIVITY_SUCCESS(HttpStatus.OK, "활동 로그를 조회했습니다."),
    DASHBOARD_STATS_FETCHED(HttpStatus.OK, "대시보드 통계 조회 완료"),
    DASHBOARD_MY_TASKS_SUCCESS(HttpStatus.OK, "내 작업 요약 조회 완료"),
    DASHBOARD_WEEKLY_TRENDS_SUCCESS(HttpStatus.OK, "주간 작업 추세 조회 완료"),

    // --- Search Success ---
    SEARCH_SUCCESS(HttpStatus.OK, "검색이 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
