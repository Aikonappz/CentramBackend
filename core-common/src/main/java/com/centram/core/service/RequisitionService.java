package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.*;
import com.centram.domain.*;
import com.centram.domain.enumarator.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class RequisitionService {

    @Autowired
    private RequisitionRepository requisitionRepository;

    @Autowired
    private RequisitionManagerReviewRepository requisitionManagerReviewRepository;

    @Autowired
    RequisitionRecruiterTeamLeadRepository requisitionRecruiterTeamLeadRepository;

    @Autowired
    RequisitionRecruiterReviewRepository requisitionRecruiterReviewRepository;

    @Autowired
    RequisitionCompletedRepository requisitionCompletedRepository;

    @Autowired
    RequisitionMapper requisitionMapper;

    @Autowired
    RequisitionManagerReviewMapper requisitionManagerReviewMapper;

    @Autowired
    RequisitionRecruiterTeamLeadMapper requisitionRecruiterTeamLeadMapper;

    @Autowired
    RequisitionRecruiterReviewMapper requisitionRecruiterReviewMapper;

    @Autowired
    RequisitionCompletedMapper requisitionCompletedMapper;

    @Autowired
    NotificationService notificationService;

    @Transactional
    public Requisition saveRequisition(Requisition requisition) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(requisition.getId() != null){
            requisition =  updateRequisition(requisition);
        }
        Requisition result = requisitionRepository.save(requisition);
//        notificationService.sendNotificationFromRequisition(result);
        return result;
    }

    public Requisition updateRequisition(Requisition requisition) {
        Requisition existing = requisitionRepository.findById(requisition.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(requisition, existing, requisitionMapper);
        return existing;
    }

    public PaginatedList<Requisition> getAllRequisition(String name, Status status, Pageable pageable) {
        return new PaginatedList<Requisition>(requisitionRepository.findAll(pageable));
    }

    public Requisition getByRequisitionId(BigInteger id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public RequisitionManagerReview saveRequisitionManagerReview(RequisitionManagerReview requisitionManagerReview) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requisitionManagerReview.getRequisition() != null && requisitionManagerReview.getRequisition().getId() != null) {
            Requisition requisition = requisitionRepository.findById(requisitionManagerReview.getRequisition().getId())
                    .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
            requisitionManagerReview.setRequisition(requisition);
        }

        if (requisitionManagerReview.getId() != null) {
            requisitionManagerReview = updateReview(requisitionManagerReview);
        }

        return requisitionManagerReviewRepository.save(requisitionManagerReview);
    }

    public RequisitionManagerReview updateReview(RequisitionManagerReview review) {
        RequisitionManagerReview existing = requisitionManagerReviewRepository.findById(review.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(review, existing, requisitionManagerReviewMapper);
        return existing;
    }

    public RequisitionManagerReview getByRequisitionMangerReviewId(BigInteger id) {
        return requisitionManagerReviewRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public RequisitionRecruiterTeamLead saveRequisitionRecruiterTeamLead(RequisitionRecruiterTeamLead requisitionRecruiterTeamLead) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requisitionRecruiterTeamLead.getRequisition() != null && requisitionRecruiterTeamLead.getRequisition().getId() != null) {
            Requisition requisition = requisitionRepository.findById(requisitionRecruiterTeamLead.getRequisition().getId())
                    .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
            requisitionRecruiterTeamLead.setRequisition(requisition);
        }

        if (requisitionRecruiterTeamLead.getId() != null) {
            requisitionRecruiterTeamLead = updateTeamLead(requisitionRecruiterTeamLead);
        }

        return requisitionRecruiterTeamLeadRepository.save(requisitionRecruiterTeamLead);
    }

    public RequisitionRecruiterTeamLead updateTeamLead(RequisitionRecruiterTeamLead teamLead) {
        RequisitionRecruiterTeamLead existing = requisitionRecruiterTeamLeadRepository.findById(teamLead.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(teamLead, existing, requisitionRecruiterTeamLeadMapper);
        return existing;
    }

    public RequisitionRecruiterTeamLead getByRequisitionRecruiterTeamLeadId(BigInteger id) {
        return requisitionRecruiterTeamLeadRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public RequisitionRecruiterReview saveRequisitionRecruiterReview(RequisitionRecruiterReview requisitionRecruiterReview) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requisitionRecruiterReview.getRequisition() != null && requisitionRecruiterReview.getRequisition().getId() != null) {
            Requisition requisition = requisitionRepository.findById(requisitionRecruiterReview.getRequisition().getId())
                    .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
            requisitionRecruiterReview.setRequisition(requisition);
        }

        if (requisitionRecruiterReview.getId() != null) {
            requisitionRecruiterReview = updateReview(requisitionRecruiterReview);
        }

        return requisitionRecruiterReviewRepository.save(requisitionRecruiterReview);
    }

    public RequisitionRecruiterReview updateReview(RequisitionRecruiterReview review) {
        RequisitionRecruiterReview existing = requisitionRecruiterReviewRepository.findById(review.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(review, existing, requisitionRecruiterReviewMapper);
        return existing;
    }

    public RequisitionRecruiterReview getByRequisitionRecruiterReviewId(BigInteger id) {
        return requisitionRecruiterReviewRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public RequisitionCompleted saveRequisitionCompleted(RequisitionCompleted completed) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (completed.getRequisition() != null && completed.getRequisition().getId() != null) {
            Requisition requisition = requisitionRepository.findById(completed.getRequisition().getId())
                    .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
            completed.setRequisition(requisition);
        }

        if (completed.getId() != null) {
            completed = updateComplete(completed);
        }

        return requisitionCompletedRepository.save(completed);
    }

    public RequisitionCompleted updateComplete(RequisitionCompleted completed) {
        RequisitionCompleted existing = requisitionCompletedRepository.findById(completed.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(completed, existing, requisitionCompletedMapper);
        return existing;
    }

    public RequisitionCompleted getByRequisitionCompletedId(BigInteger id) {
        return requisitionCompletedRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }
}
