package org.devlogtwo.devlog.domain.actlog.specs;

import java.time.LocalDate;
import java.time.LocalTime;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.springframework.data.jpa.domain.Specification;

public class ActivityLogSpecs {

    public static Specification<ActivityLog> withType(ActivityType type) {
        return (root, query, builder) -> builder.equal(root.get("type"), type);
    }

    public static Specification<ActivityLog> withUserId(Long userId) {
        return (root, query, builder) -> builder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<ActivityLog> withTaskId(Long taskId) {
        return (root, query, builder) -> builder.equal(root.get("taskId"), taskId);
    }

    public static Specification<ActivityLog> withDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, builder) -> builder.between(
                root.get("createdAt"),
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );
    }
}
