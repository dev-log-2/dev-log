package org.devlogtwo.devlog.domain.task.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record TaskPageResponse(
        List<TaskResponse> content,
        Long totalElements,
        int totalPages,
        int size,
        int number
) {

    public static TaskPageResponse from(Page<TaskResponse> page) {
        return new TaskPageResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }
}
