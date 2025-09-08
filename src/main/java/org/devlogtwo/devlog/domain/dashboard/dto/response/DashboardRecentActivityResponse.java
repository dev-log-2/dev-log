package org.devlogtwo.devlog.domain.dashboard.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.devlogtwo.devlog.domain.user.entity.User;

public record DashboardRecentActivityResponse(
        Long id,
        Long userId,
        UserInfo user,
        String action,
        String targetType,
        Long targetId,
        String description,
        LocalDateTime createdAt
) {
    public static DashboardRecentActivityResponse of(ActivityLog activityLog, String description) {

        String targetType;
        ActivityType activityType = activityLog.getType();

        if (activityType.name().contains("COMMENT")) {
            targetType = "comment";
        } else {
            targetType = "task";
        }

        return new DashboardRecentActivityResponse(
                activityLog.getId(), activityLog.getUser() != null ? activityLog.getUser().getId() : null,
                UserInfo.from(activityLog.getUser()),
                activityLog.getType().name(), targetType, activityLog.getTaskId(),
                description,
                activityLog.getCreatedAt());
    }

    public record UserInfo(
            Long id,
            String name
    ) {
        public static UserInfo from(User user) {
            if (user == null) {
                return null;
            }

            return new UserInfo(user.getId(), user.getName());
        }
    }
}
