package com.centram.core.repository;


import com.centram.domain.ProjectUat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ProjectUatRepository extends JpaRepository<ProjectUat, BigInteger> {
}