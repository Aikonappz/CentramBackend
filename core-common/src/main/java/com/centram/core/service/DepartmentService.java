package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.DepartmentVO;
import com.centram.core.repository.DepartmentRepository;
import com.centram.domain.Department;
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
    private ProxyService proxyService;


    /**
     * get department
     *
     * @param departmentId
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "departments", key = "#departmentId")
    public Department getById(BigInteger departmentId) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        if (!department.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return department.get();
    }

    /**
     * @param departmentName
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "departments", key = "#departmentId")
    public Department getByDepartmentName(String departmentName) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return departmentRepository.getDepartmentByName(departmentName, loggedInUser.getOrganisationId());
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

    @Transactional(readOnly = true)
    public List<DepartmentVO> getDepartments(BigInteger id) {
        log.info("Puling department data for {}.",id);
        return departmentRepository.getDepartmentByOrganisation(id);
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
        return departmentRepository.save(department);
    }

    private Department convert(Department department, DepartmentVO departmentVO) {
        department.setName(departmentVO.getName());
        department.setStatus(Status.valueOf(departmentVO.getStatus()));
        return department;
    }

    public void saveAll(List<DepartmentVO> departments, BigInteger id) {
        log.info("Saving department data for {}.",id);
        Optional<Department> optDepartment = Optional.empty();
        Department dept = null;
        if (departments.size() > 0) {
            for (DepartmentVO department : departments) {
                try {
                    if (department.getId() != null) {
                        optDepartment = proxyService.getDepartment(department.getId());
                        if (optDepartment.isPresent()) {
                            dept = this.convert(optDepartment.get(), department);
                            log.info("Saving department data {}.",dept);
                            dept = proxyService.saveDepartment(dept);
                        } else {
                            dept = this.convert(new Department(), department);
                            dept.setOrganisation(organisationService.getOrganisationById(id));
                            log.info("Saving department data {}.",dept);
                            dept = proxyService.saveDepartment(dept);
                        }
                    } else {
                        dept = this.convert(new Department(), department);
                        dept.setOrganisation(organisationService.getOrganisationById(id));
                        log.info("Saving department data {}.",dept);
                        dept = proxyService.saveDepartment(dept);
                    }
                } catch (Exception e) {
                    //log.error(e.getStackTrace().toString());
                    //throw e;
                    continue;
                }
            }
        }
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

    }
}
