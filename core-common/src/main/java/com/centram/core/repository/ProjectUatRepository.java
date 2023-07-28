package com.centram.core.repository;


import com.centram.domain.ProjectUat;
import com.centram.domain.ProjectUatScript;
import com.centram.domain.ProjectUatScriptDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface ProjectUatRepository extends JpaRepository<ProjectUat, BigInteger> {

    @Query("select pu.projectUatScripts from ProjectUat pu where pu.project.id = (:projectId) and pu.moduleId = (:moduleId) and pu.subModuleId = (:subModuleId)")
    List<Set<ProjectUatScript>> findByProjectIdAndModuleIdAndSubModuleId(@Param("projectId") BigInteger projectId, @Param("moduleId") BigInteger moduleId, @Param("subModuleId") BigInteger subModuleId);

    @Query("select pusd from ProjectUat pu join pu.projectUatScripts pus join pus.projectUatScriptDetails pusd " +
            " where pu.project.id = (:projectId) and pu.moduleId = (:moduleId) and pu.subModuleId = (:subModuleId) and pus.id = (:projectUATScriptId) ")
    Page<ProjectUatScriptDetail> findByProjectIdAndModuleIdAndSubModuleIdAndProjectUATScriptId(
            @Param("projectId") BigInteger projectId,
            @Param("moduleId") BigInteger moduleId,
            @Param("subModuleId") BigInteger subModuleId,
            @Param("projectUATScriptId") BigInteger projectUATScriptId,
            @Param("pageable") Pageable pageable
    );
}