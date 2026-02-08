package com.centram.core.repository;

import com.centram.domain.JobPortalCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface JobPortalCandidateRepository extends JpaRepository<JobPortalCandidate, BigInteger> {

    Optional<JobPortalCandidate> findByEmail(String email);

    boolean existsByEmail(String email);
}