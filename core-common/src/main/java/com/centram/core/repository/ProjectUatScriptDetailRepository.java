package com.centram.core.repository;


import com.centram.domain.ProjectUatScriptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ProjectUatScriptDetailRepository extends JpaRepository<ProjectUatScriptDetail, BigInteger> {
}