package com.centram.core.repository;


import com.centram.domain.ProjectUatScript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ProjectUatScriptRepository extends JpaRepository<ProjectUatScript, BigInteger> {
}