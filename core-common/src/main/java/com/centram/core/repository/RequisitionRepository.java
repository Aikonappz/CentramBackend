package com.centram.core.repository;

import com.centram.domain.Requisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, BigInteger> {

    Optional<Requisition> findById(BigInteger id);
}
