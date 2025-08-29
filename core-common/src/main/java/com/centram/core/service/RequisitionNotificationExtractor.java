package com.centram.core.service;

import com.centram.common.dto.NotificationContext;
import com.centram.core.repository.NotificationExtractor;
import com.centram.core.repository.PositionRepository;
import com.centram.core.repository.UserRepository;
import com.centram.domain.Position;
import com.centram.domain.Requisition;
import com.centram.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequisitionNotificationExtractor implements NotificationExtractor<Requisition> {

    @Autowired
    PositionRepository positionRepository;
    @Autowired
    UserRepository userRepository;


    @Override
    public List<NotificationContext> extract(Requisition requisition, String status) {
        Position position = positionRepository.findById(requisition.getPositionId())
                .orElseThrow(() -> new RuntimeException("Position not found"));

        User user = userRepository.findByFullName(position.getRecruiterName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return List.of(new NotificationContext(user, "Requisition Assigned", "You have a new requisition."));
    }
}
