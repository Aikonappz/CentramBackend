package com.centram.core.repository;


import com.centram.domain.Department;
import com.centram.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DepartmentRepository extends PagingAndSortingRepository<Department, BigInteger> {
    @Query("select d from Department d where d.organisation.id = (:organisationId)")
    Page getDepartmentByOrganisation(@Param("organisationId") BigInteger organisationId, @Param("pageable") Pageable pageable);
    @Modifying
    @Query("update Department set status = (:status) where id in (:departmentIds)")
    Integer updateStatus(@Param("status") Status status, @Param("departmentIds") List<BigInteger> departmentIds);
}
