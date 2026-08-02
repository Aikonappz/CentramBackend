package com.centram.core.repository;

import com.centram.domain.Requisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigInteger;

@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, BigInteger> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT MAX(r.id) FROM Requisition r")
    BigInteger findMaxPositionCode();
}
