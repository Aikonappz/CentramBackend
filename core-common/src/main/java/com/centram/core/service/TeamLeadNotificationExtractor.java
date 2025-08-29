package com.centram.core.service;

import com.centram.common.dto.NotificationContext;
import com.centram.core.repository.NotificationExtractor;
import com.centram.core.repository.PositionRepository;
import com.centram.core.repository.RequisitionRepository;
import com.centram.core.repository.UserRepository;
import com.centram.domain.Requisition;
import com.centram.domain.RequisitionRecruiterTeamLead;
import com.centram.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamLeadNotificationExtractor implements NotificationExtractor<RequisitionRecruiterTeamLead> {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<NotificationContext> extract(RequisitionRecruiterTeamLead lead, String status) {
        Requisition requisition = lead.getRequisition();
        User user = userRepository.findByFullName(requisition.getHiringManager())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return List.of(new NotificationContext(user, "Team Lead Assigned", "You were assigned to a requisition."));
    }
}
