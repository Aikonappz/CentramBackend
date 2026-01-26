package com.centram.core.service;

import com.centram.common.dto.NotificationContext;
import com.centram.core.repository.NotificationExtractor;
import com.centram.core.repository.PositionRepository;
import com.centram.core.repository.UserRepository;
import com.centram.domain.Position;
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

    @Autowired
    PositionRepository positionRepository;

    @Override
    public List<NotificationContext> extract(RequisitionRecruiterReview recruiterReview, String status, String name) {
        Requisition requisition = recruiterReview.getRequisition();
        User backwardUser = userRepository.findByFullName(requisition.getHeadOfRecruitment())
                .orElseThrow(() -> new RuntimeException("Recruiter Review Head of Recruitment not found"));

        User currentBackwardUser = userRepository.findByFullName(name)
                .orElseThrow(() -> new RuntimeException("Recruiter Review User not found"));

        Position position = positionRepository.findById(recruiterReview.getRequisition().getPositionId())
                .orElseThrow(() -> new RuntimeException("Recruiter Review Position not found"));
        User recruiter = userRepository.findByFullName(position.getRecruiterName())
                .orElseThrow(() -> new RuntimeException("Recruiter Review Recruiter Name  not found"));
        User hiringManager = userRepository.findByFullName(requisition.getHiringManager())
                .orElseThrow(() -> new RuntimeException("Recruiter Review Hiring Manager not found"));


        Map<String, String> backwardUserPlaceHolders = Map.of(
                "USER_NAME", requisition.getHeadOfRecruitment(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "CREATOR_NAME", name,
                "REQ_LINK", "http://localhost:3000/job-requisition/correction?reqId=" + requisition.getId() + "&stepper=3"
        );

        Map<String, String> currentUserBackwardPlaceholders = Map.of(
                "USER_NAME", name,
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle()
        );

        Map<String, String> requisitionCompletedRecruiterPlaceholders = Map.of(
                "USER_NAME", position.getRecruiterName(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "REQ_LINK", "http://localhost:3000/create/job-requisition?reqId=" + requisition.getId() + "&stepper=4"
        );

        Map<String, String> requisitionCompletedHiringManagerPlaceholders = Map.of(
                "USER_NAME", requisition.getHiringManager(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "REQ_LINK", "http://localhost:3000/create/job-requisition?reqId=" + requisition.getId() + "&stepper=4"
        );

        Map<String, String> requisitionCompletedRecruitmentPlaceholders = Map.of(
                "USER_NAME", requisition.getHeadOfRecruitment(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "REQ_LINK", "http://localhost:3000/create/job-requisition?reqId=" + requisition.getId() + "&stepper=4"
        );

        Map<String, String> requisitionCompletedBusinessUnitPlaceholders = Map.of(
                "USER_NAME", requisition.getHeadOfBusinessUnit(),
                "REQ_ID", String.valueOf(requisition.getId()),
                "JOB_TITLE", requisition.getJobTitle(),
                "REQ_LINK", "http://localhost:3000/create/job-requisition?reqId=" + requisition.getId() + "&stepper=4"
        );


        if (status.equals("Approver 4")) {
            return List.of(new NotificationContext(recruiter, requisitionCompletedRecruiterPlaceholders, "REQUISITION_COMPLETED_EMAIL_TEMPLATE"),
                    new NotificationContext(hiringManager, requisitionCompletedHiringManagerPlaceholders, "REQUISITION_COMPLETED_EMAIL_TEMPLATE"),
                    new NotificationContext(backwardUser, requisitionCompletedRecruitmentPlaceholders, "REQUISITION_COMPLETED_EMAIL_TEMPLATE"),
                    new NotificationContext(currentBackwardUser, requisitionCompletedBusinessUnitPlaceholders, "REQUISITION_COMPLETED_EMAIL_TEMPLATE")
            );
        } else {
            return List.of(new NotificationContext(backwardUser, backwardUserPlaceHolders, "REQUISITION_CORRECTION_EMAIL_TEMPLATE"),
                    new NotificationContext(currentBackwardUser, currentUserBackwardPlaceholders, "REQUISITION_ROUTED_BACK_EMAIL_TEMPLATE"));
        }

    }
}
