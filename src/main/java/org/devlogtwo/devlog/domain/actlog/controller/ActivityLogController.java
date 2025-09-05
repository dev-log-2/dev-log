package org.devlogtwo.devlog.domain.actlog.controller;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.SuccessCode;
import org.devlogtwo.devlog.common.dto.GlobalApiResponse;
import org.devlogtwo.devlog.common.dto.PageResponse;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.common.util.ResponseHelper;
import org.devlogtwo.devlog.domain.actlog.dto.response.ActivityLogResponse;
import org.devlogtwo.devlog.domain.actlog.service.ActivityLogService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/api/activities")
    public ResponseEntity<GlobalApiResponse<PageResponse<ActivityLogResponse>>> getActivities(
            @RequestParam(required = false) ActivityType type,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        PageResponse<ActivityLogResponse> logPage = activityLogService.getActivityLogs(type, userId, taskId,
                startDate, endDate, pageable);
        return ResponseHelper.success(SuccessCode.ACTIVITY_LOG_SUCCESS, logPage);
    }
}
