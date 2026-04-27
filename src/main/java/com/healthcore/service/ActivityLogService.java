package com.healthcore.service;

import com.healthcore.entity.ActivityLog;
import com.healthcore.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void log(String actor, String action) {
        ActivityLog log = ActivityLog.builder()
                .actor(actor)
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();
        activityLogRepository.save(log);
    }
}
