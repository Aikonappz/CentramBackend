package com.centram.core.repository;

import com.centram.domain.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface JobRoleRepository extends JpaRepository<JobRole, BigInteger> {

    @Query("SELECT DISTINCT j.jobCodeId FROM JobRole j WHERE j.jobCodeId IS NOT NULL")
    List<String> findAllDistinctJobCodes();
}
