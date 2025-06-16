package com.centram.core.repository;

import com.centram.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PositionRepository extends JpaRepository<Position, BigInteger> {
}
