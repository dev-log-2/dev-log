package org.devlogtwo.devlog.domain.actlog.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final ObjectMapper objectMapper;

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

        Object result = null;
        boolean success = true;
        Exception exceptionHandler = null;

        try {
            result = joinPoint.proceed(); // 2. 대상 메서드 실행
//            return result;
        } catch (Exception e) {
            success = false;
            result = e.getClass().getSimpleName() + ": " + e.getMessage();
            exceptionHandler = e;
            throw e;
        } finally {
            recordLog(joinPoint, activityLogger, currentUserPrincipal, success, beforeState, result, exceptionHandler);
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

    private void recordLog(ProceedingJoinPoint joinPoint, ActivityLogger logger, UserPrincipal principal,
                           boolean success, String beforeState, Object result, Exception exception) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.toShortString();
            String params = getParamsAsString(joinPoint.getArgs());

            Long taskId = extractId(joinPoint, result, "taskId");
            Long commentId = extractId(joinPoint, result, "commentId");
            String exceptionStr = (exception != null) ? exception.getClass().getSimpleName() : "None";

            String resultStr;
            if (success) {
                // 성공 시, void 메서드(result=null)는 JSON "null"로, 나머지는 객체를 JSON으로 변환
                resultStr = (result != null) ? objectMapper.writeValueAsString(result) : "null";
            } else {
                // 실패 시, 예외 정보를 JSON 문자열로 변환
                resultStr = objectMapper.writeValueAsString(
                        exception.getClass().getSimpleName() + ": " + exception.getMessage());
            }

            if (logger.type() == ActivityType.TASK_STATUS_CHANGED && result instanceof TaskResponse) {
                Map<String, Object> statusChangeMap = Map.of(
                        "before", beforeState,
                        "after", result
                );
                resultStr = objectMapper.writeValueAsString(statusChangeMap);
            }

            log.info(
                    "[ActivityLog] [User: {}, Method: {}, Params: {}, Success: {}, ExecutionTime: {}ms, Exception: {}]",
                    principal.username(),
                    methodName,
                    params,
                    success,
                    LocalDateTime.now(),
                    exceptionStr
            );
            activityLogService.saveLog(principal.id(), logger.type(), methodName, params, success, resultStr,
                    taskId, commentId);
        } catch (Exception e) {
            log.error("[ActivityLogAspect] 로그 기록 중 오류 발생", e);
        }
    }

    private String getBeforeState(ActivityType type, ProceedingJoinPoint joinPoint) {

        if (type != ActivityType.TASK_STATUS_CHANGED) {
            return null;
        }

        Long taskId = findArgumentByName(joinPoint, "taskId", Long.class);

        // 결국 이전 상태를 알기 위해서는 task db 조회 필요
        if (taskId != null) {
            return taskService.findTaskById(taskId).getStatus().name();
        }
        return null;
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

    private String getParamsAsString(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        try {
            return Arrays.stream(args)
                    .map(obj -> {
                        try {
                            // DTO 객체 등에 password 필드가 포함된 경우를 고려하여 문자열 검사
                            String jsonStr = objectMapper.writeValueAsString(obj);
                            if (jsonStr.toLowerCase().contains("\"password\"")) {
                                return "FILTERED_SENSITIVE_INFO";
                            }
                            return jsonStr;
                        } catch (Exception e) {
                            // 직렬화할 수 없는 객체는 클래스 이름만 로깅
                            return obj.getClass().getSimpleName();
                        }
                    })
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            log.warn("[ActivityLogAspect] 파라미터를 문자열로 변환하는 중 오류 발생", e);
            return "ParamConversionError";
        }
    }
}