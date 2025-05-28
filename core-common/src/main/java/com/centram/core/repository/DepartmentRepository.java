package com.centram.core.repository;


import com.centram.common.dto.CommonProjection;
import com.centram.common.vo.DepartmentVO;
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
    @Query("select d from Department d where UPPER(d.name) = UPPER((:departmentName)) and d.organisationId = (:organisationId)")
    Department getDepartmentByName(@Param("departmentName") String departmentName, @Param("organisationId") BigInteger organisationId);

    @Query("select d from Department d where d.organisationId = (:organisationId)")
    Page<Department> getDepartmentByOrganisation(@Param("organisationId") BigInteger organisationId, @Param("pageable") Pageable pageable);

    @Query("select new com.centram.common.vo.DepartmentVO(d) from Department d where d.organisationId = (:organisationId)")
    List<DepartmentVO> getDepartmentByOrganisation(@Param("organisationId") BigInteger organisationId);

    @Modifying
    @Query("update Department set status = (:status) where id in (:departmentIds)")
    Integer updateStatus(@Param("status") Status status, @Param("departmentIds") List<BigInteger> departmentIds);

    @Query("SELECT d.id AS id, d.name AS name, d.version AS version, d.status AS status FROM Department d")
    Page<CommonProjection> findAllBy(Pageable pageable);

}
