package com.centram.core.service;


import com.centram.core.repository.ActivityLogRepository;
import com.centram.domain.ActivityLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class ActivityLogService {
    private static final Logger log = LoggerFactory.getLogger(ActivityLogService.class);

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Transactional
    @Async("asyncExecutor")
    public ActivityLog save(ActivityLog activityLog) {
        return new ActivityLog();
        //return activityLogRepository.save(activityLog);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLog> getActivities(BigInteger userId, Pageable pageable) {
        return activityLogRepository.getActivities(userId, pageable);
    }
}
