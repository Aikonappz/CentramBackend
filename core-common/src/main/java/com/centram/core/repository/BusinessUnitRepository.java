package com.centram.core.repository;


import com.centram.common.dto.CommonProjection;
import com.centram.common.vo.DepartmentVO;
import com.centram.domain.BusinessUnit;
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
public interface BusinessUnitRepository extends PagingAndSortingRepository<BusinessUnit, BigInteger> {
    @Query("SELECT b.id AS id, b.name AS name, b.version AS version, b.status AS status FROM BusinessUnit b")
    Page<CommonProjection> findAllBy(Pageable pageable);
}
