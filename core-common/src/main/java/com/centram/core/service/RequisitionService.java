package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.RequisitionManagerReviewMapper;
import com.centram.core.repository.RequisitionManagerReviewRepository;
import com.centram.core.repository.RequisitionMapper;
import com.centram.core.repository.RequisitionRepository;
import com.centram.domain.Requisition;
import com.centram.domain.RequisitionManagerReview;
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
    RequisitionMapper requisitionMapper;

    @Autowired
    RequisitionManagerReviewMapper requisitionManagerReviewMapper;

    @Transactional
    public Requisition saveRequisition(Requisition requisition) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(requisition.getId() != null){
            requisition =  updateRequisition(requisition);
        }
        return requisitionRepository.save(requisition);
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
}
