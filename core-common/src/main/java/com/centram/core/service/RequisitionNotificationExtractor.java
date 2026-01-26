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
        Position position = positionRepository.findById(requisition.getPositionId())
                .orElseThrow(() -> new RuntimeException("Requisition notification Position not found"));
        User hiringManager = userRepository.findByFullName(requisition.getHiringManager())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User recruiter = userRepository.findByFullName(position.getRecruiterName())
                .orElseThrow(() -> new RuntimeException("Requisition notification Recruiter Name  not found")); //current user

        Map<String, String> hiringManagerPlaceholders = Map.of(
                "USER_NAME", requisition.getHiringManager(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "CREATOR_NAME", name,
                "REQ_LINK", "http://localhost:3000/create/job-requisition?reqId=" + requisition.getId() + "&stepper=1"
        );

        Map<String, String> recruiterPlaceholders = Map.of(
                "USER_NAME", position.getRecruiterName(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle()
        );

        return List.of(new NotificationContext(hiringManager, hiringManagerPlaceholders, "REQUISITION_CREATED_EMAIL_TEMPLATE"),
                new NotificationContext(recruiter, recruiterPlaceholders, "REQUISITION_ROUTED_FORWARD_EMAIL_TEMPLATE"));
    }
}
