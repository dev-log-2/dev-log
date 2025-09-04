package org.devlogtwo.devlog.domain.task.dto.request;

import org.devlogtwo.devlog.common.type.TaskStatus;

public record TaskStatusUpdateRequest(
        TaskStatus status
) {
}
