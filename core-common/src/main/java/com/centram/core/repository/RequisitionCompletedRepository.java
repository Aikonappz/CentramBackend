package com.centram.core.repository;

import com.centram.domain.RequisitionCompleted;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RequisitionCompletedRepository extends JpaRepository<RequisitionCompleted, BigInteger> {
}
