package com.centram.core.repository;

import com.centram.domain.IncidentNotification;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;


@Repository
public class IncidentNotificationRepository {

    private final String hashReference = "IncidentNotification";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;

    public void save(IncidentNotification obj) {
        String key = String.valueOf(obj.getIncidentId()).concat("_").concat(obj.getIncidentNotificationType().name());
        hashOperations.putIfAbsent(hashReference, key, obj);
    }

    public IncidentNotification find(IncidentNotification obj) {
        String key = String.valueOf(obj.getIncidentId()).concat("_").concat(obj.getIncidentNotificationType().name());
        return (IncidentNotification) hashOperations.get(hashReference, key);
    }

    /*public void saveAll(Map<String, IncidentNotification> incidentNotificationMap) {
        hashOperations.putAll(hashReference, incidentNotificationMap);
    }



    public Map<String, IncidentNotification> findAll() {
        Map<String, IncidentNotification> incidentNotificationMap = new HashMap<String, IncidentNotification>();
        for (Map.Entry<String, Object> entry : hashOperations.entries(hashReference).entrySet()) {
            incidentNotificationMap.put(entry.getKey(), (IncidentNotification) entry.getValue());
        }
        return incidentNotificationMap;
    }

    public void delete(String key) {
        hashOperations.delete(hashReference, key);
    }*/
}