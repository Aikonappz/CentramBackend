package com.centram.core.repository;

import com.centram.domain.JobProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface JobProfileRepository extends JpaRepository<JobProfile, BigInteger> {
    @Query("SELECT jp FROM JobProfile jp WHERE jp.jobRole.jobCodeId = :jobCodeId")
    List<JobProfile> findByJobCodeId(@Param("jobCodeId") String jobCodeId);
}

