package com.centram.core.repository;


import com.centram.common.dto.UatScriptReportDTO;
import com.centram.common.vo.CategoryAdminDashboardVO;
import com.centram.common.vo.IncidentPriorityVO;
import com.centram.domain.Incident;
import com.centram.domain.ProjectUat;
import com.centram.domain.ProjectUatScript;
import com.centram.domain.ProjectUatScriptDetail;
import com.centram.domain.enumarator.Technology;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ProjectUatRepository extends JpaRepository<ProjectUat, BigInteger> {

    @Query("select pu from ProjectUat pu where pu.uploadedBy.id = (:loggedInUser) ")
    Page<ProjectUat> uploadedScripts(
            @Param("loggedInUser") BigInteger loggedInUser,
            @Param("pageable") Pageable pageable
    );

    @Query("select pu from ProjectUat pu where pu.project.id = (:projectId) and pu.moduleId = (:moduleId) and pu.subModuleId = (:subModuleId)")
    List<ProjectUat> getByProjectIdAndModuleIdAndSubModuleId(@Param("projectId") BigInteger projectId, @Param("moduleId") BigInteger moduleId, @Param("subModuleId") BigInteger subModuleId);

    @Query("select pus from ProjectUat pu join pu.projectUatScripts pus where pu.id = (:uatProjectId)")
    Set<ProjectUatScript> getProjectUatScriptsByUatProjectId(@Param("uatProjectId") BigInteger uatProjectId);

    @Query("select pus from ProjectUat pu join pu.projectUatScripts pus where pu.id = (:uatProjectId)")
    Page<ProjectUatScript> getProjectUatScripts(
            @Param("uatProjectId") BigInteger uatProjectId,
            @Param("pageable") Pageable pageable
    );

    @Query("select pusd from ProjectUat pu join pu.projectUatScripts pus join pus.projectUatScriptDetails pusd where pus.id = (:projectUATScriptId) ")
    Page<ProjectUatScriptDetail> findByProjectUATScriptId(
            @Param("projectUATScriptId") BigInteger projectUATScriptId,
            @Param("pageable") Pageable pageable
    );

    @Query("select pu from ProjectUat pu join pu.projectUatScripts pus where pus.id = (:uatScriptId) ")
    ProjectUat findByProjectUATScriptId( @Param("uatScriptId") BigInteger uatScriptId );

    @Query("select pu from ProjectUat pu join pu.projectUatScripts pus join pus.projectUatScriptDetails pusd where pusd.id = (:projectUATScriptDetailId) ")
    ProjectUat findByProjectUATScriptDetailId(
            @Param("projectUATScriptDetailId") BigInteger projectUATScriptDetailId
    );

    @Query(value = " select p from ProjectUat p where p.createdDate BETWEEN (:start) and (:end) order by 1 asc ")
    List<ProjectUat> uatDashboard(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = "select new com.centram.common.dto.UatScriptReportDTO(pu, pus) from ProjectUat pu join pu.projectUatScripts pus " +
            " where (pu.createdDate between (:start) and (:end)) and pu.technology = (:technology) and " +
            " ( " +
            "   ((:moduleId) is not null and pu.moduleId = (:moduleId)) " +
            "   OR " +
            "   ((:moduleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:subModuleId) is not null and pu.subModuleId = (:subModuleId)) " +
            "   OR " +
            "   ((:subModuleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:projectId) is not null and pu.project.id = (:projectId)) " +
            "   OR " +
            "   ((:projectId) is null) " +
            " ) and " +
            " ( " +
            "   ((:projectUatId) is not null and pu.id = (:projectUatId)) " +
            "   OR " +
            "   ((:projectUatId) is null) " +
            " ) and " +
            " ( " +
            "   ((:uploadedByUserId) is not null and pu.uploadedBy.id = (:uploadedByUserId)) " +
            "   OR " +
            "   ((:uploadedByUserId) is null) " +
            " ) and " +
            " ( " +
            "   ((:projectUatScriptId) is not null and pus.id = (:projectUatScriptId)) " +
            "   OR " +
            "   ((:projectUatScriptId) is null) " +
            " ) "
           )
    Page<UatScriptReportDTO> uatReport(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("technology") Technology technology,
            @Param("moduleId") BigInteger moduleId,
            @Param("subModuleId") BigInteger subModuleId,
            @Param("projectId") BigInteger projectId,
            @Param("projectUatId") BigInteger projectUatId,
            @Param("projectUatScriptId") BigInteger projectUatScriptId,
            @Param("uploadedByUserId") BigInteger uploadedByUserId,
            Pageable pageable
    );



}