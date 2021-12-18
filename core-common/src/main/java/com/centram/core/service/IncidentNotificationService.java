package com.centram.core.service;


import com.centram.core.repository.IncidentNotificationRepository;
import com.centram.domain.IncidentNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IncidentNotificationService {
    private static final Logger log = LoggerFactory.getLogger(IncidentNotificationService.class);

    @Autowired
    private IncidentNotificationRepository incidentNotificationRepository;

    public void save(IncidentNotification obj) {
        incidentNotificationRepository.save(obj);
    }

    public IncidentNotification find(IncidentNotification obj) {
        return incidentNotificationRepository.find(obj);
    }
}
