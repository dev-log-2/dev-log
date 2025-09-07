package org.devlogtwo.devlog.domain.actlog.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.devlogtwo.devlog.common.annotation.ActivityLogger;
import org.devlogtwo.devlog.common.security.UserPrincipal;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.domain.actlog.service.ActivityLogService;
import org.devlogtwo.devlog.domain.comment.dto.response.CommentResponse;
import org.devlogtwo.devlog.domain.task.dto.response.TaskResponse;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.task.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLogAspect {

    private final ActivityLogService activityLogService;
    private final TaskService taskService; // Task의 변경 전 상태 조회를 위해 필요

    @Pointcut("@annotation(activityLogger)")
    public void activityLogPointcut(ActivityLogger activityLogger) {
    }

    @Around("activityLogPointcut(activityLogger)")
    public Object logActivity(ProceedingJoinPoint joinPoint, ActivityLogger activityLogger) throws Throwable {

        UserPrincipal currentUserPrincipal = getCurrentUserPrincipal();
        if (currentUserPrincipal == null) {
            log.warn("[ActivityLogAspect] 로그를 기록할 사용자 정보를 찾을 수 없어, 대상 메소드만 실행합니다.");
            return joinPoint.proceed();
        }

        // Type이 TASK_STATUS_CHANGE 경우에만 사용
        String beforeState = getBeforeState(activityLogger.type(), joinPoint);

        Object result = joinPoint.proceed();

        try {
            Long taskId = extractId(joinPoint, result, "taskId");
            Long commentId = extractId(joinPoint, result, "commentId");
            String description = createLogDescription(activityLogger.type(), result, beforeState);

            activityLogService.saveLog(currentUserPrincipal.id(), activityLogger.type(), taskId, commentId,
                    description);

            log.info("[ActivityLogAspect]: Success - [User: {}] [Type: {}] [Description: {}]",
                    currentUserPrincipal.username(),
                    activityLogger.type().name(), description);

        } catch (Exception e) {
            // 로깅으로 인해 대상 메서드가 종료되지 않도록 예외를 새로 던지지는 않음
            log.error("[ActivityLogAspect]", e);
        }

        return result;
    }

    // User 엔티티를 매 번 조회하는 대신 UserPrincipal을 사용하여 db 조회가 이뤄지지 않도록 개선
    private UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        return null;
    }

    private String getBeforeState(ActivityType type, ProceedingJoinPoint joinPoint) {

        if (type != ActivityType.TASK_STATUS_CHANGED) {
            return null;
        }

        Long taskId = findArgumentByName(joinPoint, "taskId", Long.class);

        // 결국 이전 상태를 알기 위해서는 조회 필요
        if (taskId != null) {
            Task task = taskService.findTaskById(taskId);
            return task.getStatus().name();
        }
        return null;
    }

    private String createLogDescription(ActivityType type, Object result, String beforeState) {

        if (result instanceof TaskResponse task) {
            switch (type) {
                case TASK_CREATED:
                    return String.format("새로운 작업 '%s'을 생성했습니다.", task.title());
                case TASK_UPDATED:
                    return "작업 정보를 수정했습니다.";
                case TASK_STATUS_CHANGED:
                    if (beforeState != null) {
                        return String.format("작업 상태를 '%s'에서 '%s'로 변경했습니다.", beforeState,
                                task.status());
                    }
                    return String.format("작업 상태를 '%s'로 변경했습니다.", task.status());
            }
        } else if (result instanceof CommentResponse comment) {
            switch (type) {
                case COMMENT_CREATED:
                    return "작업에 댓글을 작성했습니다.";
                case COMMENT_UPDATED:
                    return "댓글을 수정했습니다.";
            }
        } else {
            switch (type) {
                case TASK_DELETED:
                    return "작업을 삭제했습니다.";
                case COMMENT_DELETED:
                    return "댓글을 삭제했습니다.";
            }
        }

        return type.getDescription();
    }

    private Long extractId(ProceedingJoinPoint joinPoint, Object result, String idName) {

        // 반환하는 result에서 가져오기
        if (result instanceof TaskResponse task) {
            if (idName.equals("taskId")) {
                return task.id();
            } else if (idName.equals("commentId")) {
                return null;
            }
        }

        if (result instanceof CommentResponse comment) {
            if (idName.equals("commentId")) {
                return comment.id();
            } else if (idName.equals("taskId")) {
                return comment.taskId();
            }
        }

        // 삭제 메소드처럼 반환이 없는 경우 메소드 인자에서 가져오기
        return findArgumentByName(joinPoint, idName, Long.class);
    }

    // 공통으로 사용하기 위해 추상화
    private <T> T findArgumentByName(ProceedingJoinPoint joinPoint, String name, Class<T> type) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equalsIgnoreCase(name) && type.isInstance(args[i])) {
                return type.cast(args[i]);
            }
        }
        return null;
    }
}