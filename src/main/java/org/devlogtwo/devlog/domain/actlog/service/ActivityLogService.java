package org.devlogtwo.devlog.domain.actlog.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.domain.actlog.dto.response.ActivityLogResponse;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.devlogtwo.devlog.domain.actlog.repository.ActivityLogRepository;
import org.devlogtwo.devlog.domain.actlog.specs.ActivityLogSpecs;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public void saveLog(User currentUser, ActivityType type, Long taskId, Long commentId, String description) {

        ActivityLog newActivityLog = ActivityLog.create(currentUser, type, taskId, commentId, description);
        activityLogRepository.save(newActivityLog);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> getActivityLogs(ActivityType type, Long userId, Long taskId,
                                                     LocalDate startDate, LocalDate endDate,
                                                     Pageable pageable) {

        Specification<ActivityLog> spec = Specification.unrestricted();

        if (type != null) {
            spec = spec.and(ActivityLogSpecs.withType(type));
        }

        if (userId != null) {
            spec = spec.and(ActivityLogSpecs.withUserId(userId));
        }
        if (taskId != null) {
            spec = spec.and(ActivityLogSpecs.withTaskId(taskId));
        }
        if (startDate != null && endDate != null) {
            spec = spec.and(ActivityLogSpecs.withDateBetween(startDate, endDate));
        }

        return activityLogRepository.findAll(spec, pageable)
                .map(ActivityLogResponse::from);
    }
}
