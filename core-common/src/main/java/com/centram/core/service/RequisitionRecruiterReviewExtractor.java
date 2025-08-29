package com.centram.core.service;

import com.centram.common.dto.NotificationContext;
import com.centram.core.repository.NotificationExtractor;
import com.centram.core.repository.UserRepository;
import com.centram.domain.Requisition;
import com.centram.domain.RequisitionRecruiterReview;
import com.centram.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequisitionRecruiterReviewExtractor implements NotificationExtractor<RequisitionRecruiterReview> {
    @Autowired
    UserRepository userRepository;

    @Override
    public List<NotificationContext> extract(RequisitionRecruiterReview recruiterReview, String status) {
        Requisition requisition = recruiterReview.getRequisition();
        User user = userRepository.findByFullName(requisition.getHiringManager())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return List.of(new NotificationContext(user, "Recruiter Review Assigned", "You were assigned to a requisition."));
    }
}
