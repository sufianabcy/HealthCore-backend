package com.healthcore.repository;

import com.healthcore.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findAllByOrderByTimestampDesc(Pageable pageable);
    List<ActivityLog> findTop5ByOrderByTimestampDesc();
}
