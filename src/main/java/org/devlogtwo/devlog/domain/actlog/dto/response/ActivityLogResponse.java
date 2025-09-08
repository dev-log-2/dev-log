package org.devlogtwo.devlog.domain.actlog.dto.response;

import java.time.LocalDateTime;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.common.util.DescriptionGenerator;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.devlogtwo.devlog.domain.user.dto.response.UserDetailsResponse;

public record ActivityLogResponse(
        Long id,
        ActivityType type,
        Long userId,
        UserDetailsResponse user,
        Long taskId,
        LocalDateTime timestamp,
        String description
) {
    public static ActivityLogResponse from(ActivityLog log) {
        return new ActivityLogResponse(
                log.getId(),
                log.getType(),
                log.getUser().getId(),
                UserDetailsResponse.from(log.getUser()),
                log.getTaskId(),
                log.getCreatedAt(),
                DescriptionGenerator.createDescription(log)
        );
    }
}
