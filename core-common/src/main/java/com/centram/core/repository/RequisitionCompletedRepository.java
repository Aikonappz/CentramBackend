package com.centram.core.repository;

import com.centram.domain.RequisitionCompleted;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface RequisitionCompletedRepository extends JpaRepository<RequisitionCompleted, BigInteger> {
    Optional<RequisitionCompleted> findById(BigInteger id);
}
