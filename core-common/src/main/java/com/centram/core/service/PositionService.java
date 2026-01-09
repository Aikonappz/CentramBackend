package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
//import com.centram.common.dto.RecruiterDTO;
import com.centram.common.dto.PositionResponseDto;
import com.centram.common.dto.RecruiterDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.*;
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
import java.util.List;
import java.util.Optional;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    OrganisationRepository organisationRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    NotificationTrackerRepository notificationTrackerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PositionMapper positionMapper;

    @Transactional
    public Position save(Position position) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (position.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(position.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            position.setDepartment(dept);
        }
        if (position.getId() != null) {
            Department dept = departmentRepository.findById(position.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            position.setDepartment(dept);
            position = updatePosition(position);
        }

        Position savedPosition = positionRepository.save(position);
        if (position.getRecruiterName() != null) {
            User recruiter = userRepository.findByFullName(position.getRecruiterName())
                    .orElseThrow(() -> new RuntimeException("Recruiter name not found"));

            boolean alreadyExists = notificationTrackerRepository.existsByOrganisationIdAndBusinessUnitIdAndDivisionIdAndDepartmentIdAndUserId(position.getOrganisationId(), position.getBusinessUnitId(), position.getDivisionId(), position.getDepartment().getId(), recruiter.getId());

            if (!alreadyExists) {
                NotificationTracker tracker = new NotificationTracker();
                tracker.setOrganisationId(position.getOrganisationId());
                tracker.setBusinessUnitId(position.getBusinessUnitId());
                tracker.setDivisionId(position.getDivisionId());
                tracker.setDepartmentId(position.getDepartment().getId());
                tracker.setUserId(recruiter.getId());

                notificationTrackerRepository.save(tracker);
            }
        }
        return savedPosition;
    }

    public Position updatePosition(Position position) {
        Position existing = positionRepository.findById(position.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(position, existing, positionMapper);
        return existing;
    }

    @Transactional
    public PositionResponseDto getById(BigInteger id) {
        PositionResponseDto positionResponseDto = new PositionResponseDto();
        Position position =  positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id));
        Department dept = position.getDepartment();
        if (dept != null) {
            positionResponseDto.setDepartmentId(dept.getId());
            positionResponseDto.setDepartmentName(dept.getName());

            Department department = new Department();
            department.setCreatedDate(dept.getCreatedDate());
            department.setModifiedDate(dept.getModifiedDate());
            department.setVersion(dept.getVersion());
            department.setName(dept.getName());
            department.setStatus(dept.getStatus());
            positionResponseDto.setDepartment(department);

            Division division = dept.getDivision();
            if (division != null) {
                positionResponseDto.setDivisionId(division.getId());
                positionResponseDto.setDivisionName(division.getName());

                BusinessUnit bu = division.getBusinessUnit();
                if (bu != null) {
                    positionResponseDto.setBusinessUnitId(bu.getId());
                    positionResponseDto.setBusinessUnitName(bu.getName());

                  Organisation organisation =   bu.getOrganisation();
                  if(organisation != null){
                      positionResponseDto.setOrganisationId(organisation.getId());
                      positionResponseDto.setOrganisationName(organisation.getName());
                  }

                }
            }
        }
        positionResponseDto.setId(position.getId());
        positionResponseDto.setName(position.getName());
        positionResponseDto.setCode(position.getCode());
        positionResponseDto.setStatus(position.getStatus().name());
        positionResponseDto.setStartDate(position.getStartDate());
        positionResponseDto.setJobCode(position.getJobCode());
        positionResponseDto.setFte(position.getFte());
        positionResponseDto.setLocationId(position.getLocationId());
        positionResponseDto.setCostCenter(position.getCostCenter());
        positionResponseDto.setEndDate(position.getEndDate());
        positionResponseDto.setPayGrad(position.getPayGrad());
        positionResponseDto.setStandardHour(position.getStandardHour());
        positionResponseDto.setToBeHired(position.getToBeHired());
        positionResponseDto.setMinPay(position.getMinPay());
        positionResponseDto.setMidPay(position.getMidPay());
        positionResponseDto.setMaxPay(position.getMaxPay());
        positionResponseDto.setRecruiterName(position.getRecruiterName());
        if(position.getLocationId() != null) {
            Optional<Location> location = locationRepository.findById(BigInteger.valueOf(position.getLocationId()));
            positionResponseDto.setLocationName(location.map(Location::getName).orElse(null));
        }
        return positionResponseDto;
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

    public List<String> getRecruiters(RecruiterDTO dto) {
        return userRepository.findRecruiterNamesByFilters(dto.getOrganisationId(), dto.getDivisionId(), dto.getDepartmentId(), dto.getBusinessUnitId());
    }

    public PaginatedList<String> getAllUniqueJobCodes(Pageable pageable) {
        Page<String> page = positionRepository.findAllDistinctJobCodes(pageable);
        return new PaginatedList<>(page);
    }
}
