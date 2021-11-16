package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.DepartmentRepository;
import com.centram.domain.ActivityLog;
import com.centram.domain.Department;
import com.centram.domain.enumarator.ActivityType;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


@Service
public class DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ActivityLogService activityLogService;

    /**
     * get department
     *
     * @param departmentId
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "departments", key = "#departmentId")
    public Department getById(BigInteger departmentId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Department> department = departmentRepository.findById(departmentId);
        if (!department.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return department.get();
    }

    /**
     * get all departments
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Department> getDepartments(Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new PaginatedList<Department>(departmentRepository.getDepartmentByOrganisation(loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * save department
     *
     * @param department
     * @return
     */
    @Transactional
    public Department save(Department department) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        department.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, department.getId() != null ? ActivityType.ADD_DEPARTMENT : ActivityType.UPDATE_DEPARTMENT));
        return departmentRepository.save(department);
    }

    /**
     * Update department status
     *
     * @param status
     * @param userIds
     */
    @Transactional
    public void updateDepartmentsStatus(Status status, List<BigInteger> userIds) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        departmentRepository.updateStatus(status, userIds);
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.UPDATE_DEPARTMENT));
    }
}
