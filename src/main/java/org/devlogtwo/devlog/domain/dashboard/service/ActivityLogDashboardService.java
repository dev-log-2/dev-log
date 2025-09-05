package org.devlogtwo.devlog.domain.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.dto.PageResponse;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.devlogtwo.devlog.domain.actlog.service.ActivityLogServiceApi;
import org.devlogtwo.devlog.domain.dashboard.dto.response.DashboardRecentActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityLogDashboardService {

    private final ActivityLogServiceApi activityLogServiceApi;

    public PageResponse<DashboardRecentActivityResponse> getRecentActivity(Pageable pageable) {

        Page<ActivityLog> activityLogPage = activityLogServiceApi.findAllByOrderByCreatedAtDesc(pageable);

        Page<DashboardRecentActivityResponse> responsePage = activityLogPage.map(DashboardRecentActivityResponse::from);

        return PageResponse.from(responsePage);
    }
}
