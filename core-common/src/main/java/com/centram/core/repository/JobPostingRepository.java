package com.centram.core.repository;

import com.centram.domain.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, BigInteger> {

    List<JobPosting> findByRequisitionId(BigInteger requisitionId);

    List<JobPosting> findByPostingStatus(String postingStatus);
}
