package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.PositionMapper;
import com.centram.core.repository.PositionRepository;
import com.centram.domain.*;
import com.centram.domain.enumarator.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    PositionMapper positionMapper;

    @Transactional
    public Position save(Position position) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(position.getId()!=null){
            position = updatePosition(position);
        }
        return positionRepository.save(position);
    }

    public Position updatePosition(Position position) {
        Position existing = positionRepository.findById(position.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(position, existing, positionMapper);
        return existing;
    }

    @Transactional
    public Position getById(BigInteger id) {
        Position position =  positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id));
        Department dept = position.getDepartment();
        if (dept != null) {
            position.setDepartmentId(dept.getId());
            position.setOrganisationId(dept.getOrganisationId());

            Division division = dept.getDivision();
            if (division != null) {
                position.setDivisionId(division.getId());

                BusinessUnit bu = division.getBusinessUnit();
                if (bu != null) {
                    position.setBusinessUnitId(bu.getId());
                }
            }
        }

        return position;
    }

    public PaginatedList<Position> getAll(String name, Status status, Pageable pageable) {
        Page<Position> positions;
        if (status == Status.ALL) {
            positions = positionRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            positions = positionRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable);
        }

        for (Position position : positions.getContent()) {
            if (position.getDepartment() != null) {
                Department department = position.getDepartment();
                position.setDepartmentId(department.getId());

                Division division = department.getDivision();
                if (division != null) {
                    position.setDivisionId(division.getId());

                    BusinessUnit businessUnit = division.getBusinessUnit();
                    if (businessUnit != null) {
                        position.setBusinessUnitId(businessUnit.getId());

                        Organisation organisation = businessUnit.getOrganisation();
                        if (organisation != null) {
                            position.setOrganisationId(organisation.getId());
                        }
                    }
                }
            }
        }
        return new PaginatedList<>(positions);
    }

    public void deleteById(BigInteger id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
        position.setStatus(Status.INACTIVE);
        positionRepository.save(position);
    }
}
