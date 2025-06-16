package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.RequisitionMapper;
import com.centram.core.repository.RequisitionRepository;
import com.centram.domain.Requisition;
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
    RequisitionMapper requisitionMapper;

    @Transactional
    public Requisition save(Requisition requisition) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(requisition.getId() != null){
            requisition =  updateRequisition(requisition);
        }
        return requisitionRepository.save(requisition);
    }

    private Requisition updateRequisition(Requisition requisition) {
        Requisition existing = requisitionRepository.findById(requisition.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
        requisitionMapper.updateRequisitionFromRequest(existing, requisition);
        return existing;
    }

    public PaginatedList<Requisition> getAll(String name, Status status, Pageable pageable) {
        return new PaginatedList<Requisition>(requisitionRepository.findAll(pageable));
    }

    public Requisition findById(BigInteger id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }
}
