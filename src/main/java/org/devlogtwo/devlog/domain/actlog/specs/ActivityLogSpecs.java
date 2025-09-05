package org.devlogtwo.devlog.domain.actlog.specs;

import java.time.LocalDate;
import java.time.LocalTime;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.springframework.data.jpa.domain.Specification;

public class ActivityLogSpecs {

    public static Specification<ActivityLog> buildSpec(
            ActivityType type, Long userId, Long taskId, LocalDate startDate, LocalDate endDate) {

        Specification<ActivityLog> spec = Specification.unrestricted();

        if (type != null) {
            spec = spec.and(withType(type));
        }
        if (userId != null) {
            spec = spec.and(withUserId(userId));
        }
        if (taskId != null) {
            spec = spec.and(withTaskId(taskId));
        }
        if (startDate != null) {
            spec = spec.and(withStartDate(startDate));
        }

        if (endDate != null) {
            spec = spec.and(withEndDate(endDate));
        }

        return spec;
    }

    public static Specification<ActivityLog> withType(ActivityType type) {
        return (root, query, builder) ->
                builder.equal(root.get("type"), type);
    }

    public static Specification<ActivityLog> withUserId(Long userId) {
        return (root, query, builder) ->
                builder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<ActivityLog> withTaskId(Long taskId) {
        return (root, query, builder) ->
                builder.equal(root.get("taskId"), taskId);
    }

    private static Specification<ActivityLog> withStartDate(LocalDate startDate) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        startDate.atStartOfDay()
                );
    }

    private static Specification<ActivityLog> withEndDate(LocalDate endDate) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        endDate.atTime(LocalTime.MAX)
                );
    }
}
