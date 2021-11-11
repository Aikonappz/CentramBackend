package com.centram.core.service;


import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.DepartmentRepository;
import com.centram.domain.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;


@Service
public class DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "departments", key = "#departmentId")
    public Department getById(BigInteger departmentId) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        if (department.isPresent()) {
            return department.get();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public PaginatedList<Department> getDepartments(Pageable pageable) {
        return new PaginatedList<Department>(departmentRepository.findAll(pageable));
    }
}
