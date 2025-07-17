package com.centram.core.repository;

import com.centram.domain.Requisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, BigInteger> {
}
