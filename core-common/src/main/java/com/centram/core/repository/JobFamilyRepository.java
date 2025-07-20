package com.centram.core.repository;

import com.centram.domain.JobFamily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface JobFamilyRepository extends JpaRepository<JobFamily, BigInteger> {
}
