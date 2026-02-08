package com.centram.core.repository;

import com.centram.domain.JobPortalApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface JobPortalApplicationRepository extends JpaRepository<JobPortalApplication, BigInteger> {

    boolean existsByCandidateIdAndJobPostingId(BigInteger candidateId, BigInteger jobPostingId);
}

