package org.devlogtwo.devlog.domain.actlog.service;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.devlogtwo.devlog.domain.actlog.repository.ActivityLogRepository;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void saveLog(User currentUser, ActivityType type, Long taskId, Long commentId, String description) {

        ActivityLog newActivityLog = ActivityLog.create(currentUser, type, taskId, commentId, description);
        activityLogRepository.save(newActivityLog);
    }

    // TODO: 조회 요청시 별도의 메서드를 통해 반환
}
