package com.centram.core.repository;

import com.centram.domain.Competency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface CompetencyRepository extends JpaRepository<Competency, BigInteger> {
}
