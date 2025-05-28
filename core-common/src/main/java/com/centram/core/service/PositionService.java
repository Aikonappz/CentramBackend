package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.utility.PaginatedList;
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

    @Transactional
    public Position save(Position position) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return positionRepository.save(position);
    }

    @Transactional
    public Position getById(BigInteger id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id));
    }

    public PaginatedList<Position> getAll(String name, Status status, Pageable pageable) {
        Page<Position> positions = positionRepository.findAll(pageable);

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
        return new PaginatedList<>(positionRepository.findAll(pageable));
    }
}
