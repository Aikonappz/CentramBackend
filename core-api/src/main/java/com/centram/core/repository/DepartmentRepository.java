package com.centram.core.repository;


import com.centram.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, BigInteger> {
}
