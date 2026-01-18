package com.centram.core.service;

import com.centram.common.dto.NotificationContext;
import com.centram.core.repository.NotificationExtractor;
import com.centram.core.repository.UserRepository;
import com.centram.domain.Requisition;
import com.centram.domain.RequisitionRecruiterReview;
import com.centram.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RequisitionRecruiterReviewExtractor implements NotificationExtractor<RequisitionRecruiterReview> {
    @Autowired
    UserRepository userRepository;

    @Override
    public List<NotificationContext> extract(RequisitionRecruiterReview recruiterReview, String status, String name) {
        Requisition requisition = recruiterReview.getRequisition();
        User forwardUser = userRepository.findByFullName(requisition.getHiringManager())
                .orElseThrow(() -> new RuntimeException("Notification Hiring Manager not found"));

        User backwardUser = userRepository.findByFullName(requisition.getHeadOfRecruitment())
                .orElseThrow(() -> new RuntimeException("Notification Head of Recruitment not found"));

        User currentUser = userRepository.findByFullName(name)
                .orElseThrow(() -> new RuntimeException("Notification User not found"));

        String reqLink = status.equals("Approver 4")
                ? "http://localhost:3000/create/job-requisition?reqId=" + requisition.getId() + "&stepper=4"
                : "http://localhost:3000/job-requisition/correction?reqId=" + requisition.getId() + "&stepper=3";


        Map<String, String> placeholders = Map.of(
                "USER_NAME", status.equals("Approver 4") ? forwardUser.getFirstName()+" "+ forwardUser.getLastName() :
                        backwardUser.getFirstName() + " " + backwardUser.getLastName(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "CREATOR_NAME", name,
                "REQ_LINK", reqLink
        );

        if(status.equals("Approver 4")) {
            return List.of(new NotificationContext(forwardUser, placeholders, "REQUISITION_CREATED_EMAIL_TEMPLATE"),
                    new NotificationContext(currentUser, placeholders, "REQUISITION_ROUTED_FORWARD_EMAIL_TEMPLATE"));
        } else {
            return List.of(new NotificationContext(backwardUser, placeholders, "REQUISITION_CORRECTION_EMAIL_TEMPLATE"),
                    new NotificationContext(currentUser, placeholders, "REQUISITION_ROUTED_BACK_EMAIL_TEMPLATE"));
        }

    }
}
