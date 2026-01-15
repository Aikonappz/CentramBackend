package com.centram.core.repository;

import com.centram.domain.Position;
import com.centram.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PositionRepository extends JpaRepository<Position, BigInteger> {
    Page<Position> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Position> findByNameContainingIgnoreCaseAndStatus(String name, Status status, Pageable pageable);
//    @Query("SELECT DISTINCT p.jobCode FROM Position p WHERE p.jobCode IS NOT NULL")
//    Page<String> findAllDistinctJobCodes(Pageable pageable);
}
