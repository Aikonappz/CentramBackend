package com.centram.report.repository;


import com.centram.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ReportRepository extends JpaRepository<Report, BigInteger> {
}