package com.centram.core.service;

import com.centram.common.dto.NotificationContext;
import com.centram.core.repository.NotificationExtractor;
import com.centram.core.repository.PositionRepository;
import com.centram.core.repository.UserRepository;
import com.centram.domain.Position;
import com.centram.domain.Requisition;
import com.centram.domain.RequisitionCompleted;
import com.centram.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequisitionCompletedExtractor implements NotificationExtractor<RequisitionCompleted> {

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<NotificationContext> extract(RequisitionCompleted requisitionCompleted, String status) {
        Position position = positionRepository.findById(requisitionCompleted.getRequisition().getPositionId())
                .orElseThrow(() -> new RuntimeException("Position not found"));
        Requisition requisition = requisitionCompleted.getRequisition();
        User head = userRepository.findByFullName(requisition.getHeadOfRecruitment())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User recruiter = userRepository.findByFullName(position.getRecruiterName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User hiringManager = userRepository.findByFullName(requisition.getHiringManager())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return List.of(new NotificationContext(recruiter, "Requisition Completed", "Requisition Completed."),
                new NotificationContext(hiringManager, "Requisition Completed", "Requisition Completed."),
                new NotificationContext(head, "Requisition Completed", "Requisition Completed."));
    }
}
