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
import java.util.Map;

@Component
public class RequisitionNotificationExtractor implements NotificationExtractor<Requisition> {

    @Autowired
    PositionRepository positionRepository;
    @Autowired
    UserRepository userRepository;


    @Override
    public List<NotificationContext> extract(Requisition requisition, String status, String name) {
        User user = userRepository.findByFullName(requisition.getHeadOfRecruitment())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> placeholders = Map.of(
                "USER_NAME", user.getFirstName()+" "+ user.getLastName(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "CREATOR_NAME", name,
                "REQ_LINK", "http://localhost:3000/api/v1/requisition/manager_review/add"
        );

        return List.of(new NotificationContext(user, placeholders, "REQUISITION_CREATED_EMAIL_TEMPLATE"));
    }
}
