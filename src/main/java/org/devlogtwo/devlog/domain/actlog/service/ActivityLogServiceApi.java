package org.devlogtwo.devlog.domain.actlog.service;

import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityLogServiceApi {

    Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
