package org.devlogtwo.devlog.domain.actlog.repository;

import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
