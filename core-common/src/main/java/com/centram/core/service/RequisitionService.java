package com.centram.core.service;

import com.centram.common.dto.CommonProjection;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.RequisitionRepository;
import com.centram.domain.Position;
import com.centram.domain.Requisition;
import com.centram.domain.enumarator.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequisitionService {

    @Autowired
    private RequisitionRepository requisitionRepository;

    @Transactional
    public Requisition save(Requisition requisition) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return requisitionRepository.save(requisition);
    }

    public PaginatedList<Requisition> getAll(String name, Status status, Pageable pageable) {
        return new PaginatedList<Requisition>(requisitionRepository.findAll(pageable));
    }
}
