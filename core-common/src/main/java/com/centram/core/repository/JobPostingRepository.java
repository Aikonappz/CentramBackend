package com.centram.core.repository;

import com.centram.domain.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface JobPostingRepository extends JpaRepository<JobPosting, BigInteger> {
    boolean existsByRequisitionId(BigInteger requisitionId);
    Optional<JobPosting> findByRequisitionId(BigInteger requisitionId);
}
