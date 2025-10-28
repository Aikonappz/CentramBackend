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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TeamLeadNotificationExtractor implements NotificationExtractor<RequisitionRecruiterTeamLead> {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<NotificationContext> extract(RequisitionRecruiterTeamLead lead, String status, String name) {
        Requisition requisition = lead.getRequisition();
        User forwardUser = userRepository.findByFullName(requisition.getHiringManager())
                .orElseThrow(() -> new RuntimeException("Team Lead Notification Hiring Manager not found"));

        User backwardUser = userRepository.findByFullName(requisition.getHeadOfRecruitment())
                .orElseThrow(() -> new RuntimeException("Team Lead Notification Head of Recruitment not found"));

        User currentUser = userRepository.findByFullName(name)
                .orElseThrow(() -> new RuntimeException("Team Lead Notification User not found"));

        Map<String, String> placeholders = Map.of(
                "USER_NAME", status.equals("FORWARD") ? forwardUser.getFirstName()+" "+ forwardUser.getLastName() :
                        backwardUser.getFirstName() + " " + backwardUser.getLastName(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "CREATOR_NAME", name,
                "REQ_LINK", "http://localhost:7001/api/v1/requisition/recruiter_review/add  "
        );


        if(status.equals("FORWARD")) {
            return List.of(new NotificationContext(forwardUser, placeholders, "REQUISITION_CREATED_EMAIL_TEMPLATE"),
                    new NotificationContext(currentUser, placeholders, "REQUISITION_ROUTED_FORWARD_EMAIL_TEMPLATE")
            );
        } else {
            return List.of(new NotificationContext(backwardUser, placeholders, "REQUISITION_CORRECTION_EMAIL_TEMPLATE"),
                    new NotificationContext(currentUser, placeholders, "REQUISITION_ROUTED_BACK_EMAIL_TEMPLATE")
            );
        }
    }
}
