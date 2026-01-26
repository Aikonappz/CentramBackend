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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
//public class RequisitionCompletedExtractor implements NotificationExtractor<RequisitionCompleted> {
//
//    @Autowired
//    PositionRepository positionRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    public List<NotificationContext> extract(RequisitionCompleted requisitionCompleted, String status, String name) {
//        Position position = positionRepository.findById(requisitionCompleted.getRequisition().getPositionId())
//                .orElseThrow(() -> new RuntimeException("Notification Completed Position not found"));
//        Requisition requisition = requisitionCompleted.getRequisition();
//        User head = userRepository.findByFullName(requisition.getHeadOfRecruitment())
//                .orElseThrow(() -> new RuntimeException("Notification Completed Head of Recruitment not found"));
//        User recruiter = userRepository.findByFullName(position.getRecruiterName())
//                .orElseThrow(() -> new RuntimeException("Notification Completed Recruiter Name  not found"));
//        User hiringManager = userRepository.findByFullName(requisition.getHiringManager())
//                .orElseThrow(() -> new RuntimeException("Notification Completed Hiring Manager not found"));
//
//        Map<String, String> headPlaceholders = Map.of(
//                "USER_NAME", head.getFirstName()+" "+ head.getLastName(),
//                "REQ_ID", String.valueOf(requisition.getId()),
//                "JOB_TITLE", requisition.getJobTitle()
//        );
//
//        Map<String, String> recruiterPlaceholders = Map.of(
//                "USER_NAME", recruiter.getFirstName()+" "+ recruiter.getLastName(),
//                "REQ_ID", String.valueOf(requisition.getId()),
//                "JOB_TITLE", requisition.getJobTitle()
//        );
//
//        Map<String, String> hiringManagerPlaceholders = Map.of(
//                "USER_NAME", hiringManager.getFirstName()+" "+ hiringManager.getLastName(),
//                "REQ_ID", String.valueOf(requisition.getId()),
//                "JOB_TITLE", requisition.getJobTitle()
//        );
//
//        return List.of(new NotificationContext(head, headPlaceholders, "REQUISITION_COMPLETED_EMAIL_TEMPLATE"),
//                new NotificationContext(recruiter, recruiterPlaceholders, "REQUISITION_COMPLETED_EMAIL_TEMPLATE"),
//                new NotificationContext(hiringManager, hiringManagerPlaceholders, "REQUISITION_COMPLETED_EMAIL_TEMPLATE"));
//    }
//}
