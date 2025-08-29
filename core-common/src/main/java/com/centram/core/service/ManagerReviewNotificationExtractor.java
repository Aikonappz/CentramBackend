package com.centram.core.service;

import com.centram.common.dto.NotificationContext;
import com.centram.core.repository.NotificationExtractor;
import com.centram.core.repository.UserRepository;
import com.centram.domain.Requisition;
import com.centram.domain.RequisitionManagerReview;
import com.centram.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ManagerReviewNotificationExtractor implements NotificationExtractor<RequisitionManagerReview> {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<NotificationContext> extract(RequisitionManagerReview review, String status) {
        Requisition requisition = review.getRequisition();
        User user = userRepository.findByFullName(requisition.getHeadOfRecruitment())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return List.of(new NotificationContext(user, "Manager Review Submitted", "A requisition was reviewed."));
    }
}
