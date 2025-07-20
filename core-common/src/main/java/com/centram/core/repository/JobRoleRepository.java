package com.centram.core.repository;

import com.centram.domain.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface JobRoleRepository extends JpaRepository<JobRole, BigInteger> {
}
