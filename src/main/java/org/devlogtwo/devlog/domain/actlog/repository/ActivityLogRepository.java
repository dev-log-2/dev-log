package org.devlogtwo.devlog.domain.actlog.repository;

import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>, JpaSpecificationExecutor<ActivityLog> {

    @EntityGraph(value = "ActivityLog.withUser")
    Page<ActivityLog> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Override
    @EntityGraph(value = "ActivityLog.withUser")
    Page<ActivityLog> findAll(Specification<ActivityLog> spec, Pageable pageable);
}
