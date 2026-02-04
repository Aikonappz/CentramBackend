package com.centram.core.repository;

import com.centram.domain.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface JobPostingRepository extends JpaRepository<JobPosting, BigInteger> {
    boolean existsByRequisitionId(BigInteger requisitionId);
    JobPosting findByRequisitionId(BigInteger requisitionId);
}
