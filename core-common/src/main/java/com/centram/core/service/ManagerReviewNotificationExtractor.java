package com.centram.core.service;

import com.centram.common.dto.NotificationContext;
import com.centram.core.repository.NotificationExtractor;
import com.centram.core.repository.PositionRepository;
import com.centram.core.repository.UserRepository;
import com.centram.domain.Position;
import com.centram.domain.Requisition;
import com.centram.domain.RequisitionManagerReview;
import com.centram.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ManagerReviewNotificationExtractor implements NotificationExtractor<RequisitionManagerReview> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PositionRepository positionRepository;

    @Override
    public List<NotificationContext> extract(RequisitionManagerReview review, String status, String name) {
        Requisition requisition = review.getRequisition();
        User forwardUser = userRepository.findByFullName(requisition.getHeadOfRecruitment())
                .orElseThrow(() -> new RuntimeException("Manager Notification Head of Recruitment not found"));

        Optional<Position> position = positionRepository.findById(requisition.getPositionId());
        if (position.isEmpty()) {
            throw new RuntimeException("Manager Notification Position not found");
        }
        User backwardUser = userRepository.findByFullName(position.get().getRecruiterName())
                .orElseThrow(() -> new RuntimeException("Manager Notification Recruiter not found"));

        User currentUser = userRepository.findByFullName(name)
                .orElseThrow(() -> new RuntimeException("Manager Notification User not found"));

        Map<String, String> placeholders = Map.of(
                "USER_NAME", status.equals("FORWARD") ? forwardUser.getFirstName() + " " + forwardUser.getLastName() : backwardUser.getFirstName() + " " + backwardUser.getLastName(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "CREATOR_NAME", name
        );

        if(status.equals("FORWARD")) {
            return List.of(new NotificationContext(forwardUser, placeholders, "REQUISITION_CREATED_EMAIL_TEMPLATE"),
                    new NotificationContext(currentUser, placeholders, "REQUISITION_ROUTED_FORWARD_EMAIL_TEMPLATE"));
        } else {
            return List.of(new NotificationContext(backwardUser, placeholders, "REQUISITION_CORRECTION_EMAIL_TEMPLATE"),
            new NotificationContext(currentUser, placeholders, "REQUISITION_ROUTED_BACK_EMAIL_TEMPLATE"));
        }
    }
}
