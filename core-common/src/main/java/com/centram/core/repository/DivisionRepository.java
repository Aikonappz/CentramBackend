package com.centram.core.repository;


import com.centram.common.dto.CommonProjection;
import com.centram.domain.Department;
import com.centram.domain.Division;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface DivisionRepository extends PagingAndSortingRepository<Division, BigInteger> {
    @Query("SELECT d.id AS id, d.name AS name, d.version AS version, d.status AS status FROM Division d")
    Page<CommonProjection> findAllBy(Pageable pageable);
}
