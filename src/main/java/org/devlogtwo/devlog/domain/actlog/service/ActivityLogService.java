package org.devlogtwo.devlog.domain.actlog.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.dto.PageResponse;
import org.devlogtwo.devlog.common.security.UserPrincipal;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.actlog.dto.response.ActivityLogResponse;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.devlogtwo.devlog.domain.actlog.repository.ActivityLogRepository;
import org.devlogtwo.devlog.domain.actlog.specs.ActivityLogSpecs;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService implements ActivityLogServiceApi {

    private final ActivityLogRepository activityLogRepository;
    private final UserServiceApi userService;

    @Transactional
    public void saveLog(Long userId, ActivityType type, String methodName, String parameters, boolean success,
                        String result, Long taskId, Long commentId) {

        User userProxy = userService.getReferenceById(userId);
        ActivityLog newActivityLog = ActivityLog.create(userProxy, type, methodName, parameters, success, result,
                taskId, commentId);
        activityLogRepository.save(newActivityLog);
    }

    @Transactional(readOnly = true)
    public PageResponse<ActivityLogResponse> getActivityLogs(UserPrincipal principal, ActivityType type, Long userId,
                                                             Long taskId,
                                                             LocalDate startDate, LocalDate endDate,
                                                             Pageable pageable) {

        Long targetUserId = userId; // 권한이 ADMIN인 경우는 필터링 조건인 userId가 null이면 모두 조회, NULL이 아니면 userId 조회
        if (principal.role() == UserRole.USER) { // 권한이 USER인 경우는 자기 내용만 조회하도록 강제
            targetUserId = principal.id();
        }

        Specification<ActivityLog> spec = ActivityLogSpecs
                .buildSpec(type, targetUserId, taskId, startDate, endDate);

        Page<ActivityLogResponse> pages = activityLogRepository.findAll(spec, pageable)
                .map(ActivityLogResponse::from);

        return PageResponse.from(pages);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        return activityLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

}
