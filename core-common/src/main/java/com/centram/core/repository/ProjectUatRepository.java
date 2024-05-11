package com.centram.core.repository;


import com.centram.common.dto.UatScriptReportDTO;
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
    ProjectUat findByProjectIdAndModuleIdAndSubModuleId(@Param("projectId") BigInteger projectId, @Param("moduleId") BigInteger moduleId, @Param("subModuleId") BigInteger subModuleId);

    @Query("select pu from ProjectUat pu where pu.project.id = (:projectId) and pu.moduleId = (:moduleId) and pu.subModuleId = (:subModuleId)")
    List<ProjectUat> getByProjectIdAndModuleIdAndSubModuleId(@Param("projectId") BigInteger projectId, @Param("moduleId") BigInteger moduleId, @Param("subModuleId") BigInteger subModuleId);

    @Query("select pus from ProjectUat pu join pu.projectUatScripts pus join pus.customerUser user where 1 = 1 " +
            " and pu.id = (:uatProjectId) and (:customerId is null or user.id = :customerId)")
    Set<ProjectUatScript> getProjectUatScriptsByUatProjectId(@Param("uatProjectId") BigInteger uatProjectId, @Param("customerId") BigInteger customerId);

    @Query(
            value = "select pus.id from project_uat pu join project_uat_script pus on pu.id = pus.project_uat_id join project p on p.id = pu.project_id" +
            "  where  " +
            "     ( " +
            "       :uatProjectId = -1 " +
            "     OR " +
            "       :uatProjectId > 0 and pu.id = (:uatProjectId) " +
            "     ) and " +
            "     ( "  +
            "       :userType is null " +
            "     OR " +
            "       :userType = 'ADMIN' and pus.uat_complete = 1 " +
            "     OR " +
            "       :userType = 'PROJECT_MANAGER' and CONCAT(',', p.watch_list, ',') REGEXP (:emailExp) and pus.uat_complete = 1 " +
            "     OR " +
            "       :userType = 'PROJECT_OWNER' and CONCAT(',', p.stake_holders, ',') REGEXP (:emailExp)  and pus.uat_complete = 1 " +
            "     OR " +
            "       :userType = 'PROJECT_CONSULTANT' and CONCAT(',', p.consultants, ',') REGEXP (:emailExp) and pus.uat_complete = 1 " +
            "     ) ", nativeQuery = true
    )
    Page<BigInteger> getProjectUatScripts(
            @Param("userType") String userType,
            @Param("emailExp") String emailExp,
            @Param("uatProjectId") BigInteger uatProjectId,
            @Param("pageable") Pageable pageable
    );

    @Query("select pusd from ProjectUat pu join pu.projectUatScripts pus join pus.projectUatScriptDetails pusd where pus.id = (:projectUATScriptId) ")
    Page<ProjectUatScriptDetail> findByProjectUATScriptId(
            @Param("projectUATScriptId") BigInteger projectUATScriptId,
            @Param("pageable") Pageable pageable
    );

    @Query("select pus from ProjectUatScript pus where pus.id = (:projectUATScriptId) ")
    ProjectUatScript findProjectUATScriptById(
            @Param("projectUATScriptId") BigInteger projectUATScriptId
    );

    @Query("select pu from ProjectUat pu join pu.projectUatScripts pus where pus.id = (:uatScriptId) ")
    ProjectUat findByProjectUATScriptId( @Param("uatScriptId") BigInteger uatScriptId );

    @Query("select pu from ProjectUat pu join pu.projectUatScripts pus join pus.projectUatScriptDetails pusd where pusd.id = (:projectUATScriptDetailId) ")
    ProjectUat findByProjectUATScriptDetailId(
            @Param("projectUATScriptDetailId") BigInteger projectUATScriptDetailId
    );

    @Query(value = " select p from ProjectUat p where p.createdDate BETWEEN (:start) and (:end) and p.organisation.id = (:organisationId) order by 1 asc ")
    List<ProjectUat> uatDashboard(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("organisationId") BigInteger organisationId
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
    Page<UatScriptReportDTO> uatScriptReport(
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

    @Query(value = "select * from project_uat pu join project p on p.id = pu.project_id where " +
            "     (  " +
            "       (:moduleId is not null and pu.module_id = :moduleId)  " +
            "       OR  " +
            "       :moduleId is null  " +
            "     ) and  " +
            "     (  " +
            "       (:subModuleId is not null and pu.sub_module_id  = :subModuleId)  " +
            "       OR  " +
            "       :subModuleId is null  " +
            "     ) and  " +
            "     (  " +
            "       (:projectId is not null and pu.project_id  = :projectId)  " +
            "       OR  " +
            "       :projectId is null  " +
            "     ) and  " +
            "     (  " +
            "       (:projectUatId is not null and pu.id = :projectUatId)  " +
            "       OR  " +
            "       :projectUatId is null  " +
            "     ) and  " +
            "     (  " +
            "       (:uploadedByUserId is not null and pu.user_id = :uploadedByUserId)  " +
            "       OR  " +
            "       :uploadedByUserId is null  " +
            "     ) and  " +
            "     (" +
            "       :userType = 'ADMIN' " +
            "     OR " +
            "       :userType = 'PROJECT_MANAGER' and CONCAT(',', p.watch_list, ',') REGEXP (:emailExp) " +
            "     OR " +
            "       :userType = 'PROJECT_OWNER' and CONCAT(',', p.stake_holders, ',') REGEXP (:emailExp) " +
            "     OR " +
            "       :userType = 'PROJECT_CONSULTANT' and CONCAT(',', p.consultants, ',') REGEXP (:emailExp) " +
            "     ) and " +
            "     (  " +
            "       :status = 'completed' and pu.uat_cycle_complete = 1 " +
            "       OR  " +
            "       :status = 'notStarted' and pu.uat_cycle_complete = 0 and (select COUNT(*) from project_uat_script pus where pus.project_uat_id = pu.id and pus.uat_complete = 1) = 0 " +
            "       OR  " +
            "       :status = 'inProgress' and pu.uat_cycle_complete = 0 and (select COUNT(*) from project_uat_script pus where pus.project_uat_id = pu.id and pus.uat_complete = 1) > 0 " +
            "       OR  " +
            "       :status = 'total'  " +
            "     ) and pu.created_date between :start and :end and pu.technology = :technology ", nativeQuery = true)
    Page<ProjectUat> uatReport(
            @Param("userType") String userType,
            @Param("emailExp") String emailExp,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("technology") Integer technology,
            @Param("moduleId") BigInteger moduleId,
            @Param("subModuleId") BigInteger subModuleId,
            @Param("projectId") BigInteger projectId,
            @Param("projectUatId") BigInteger projectUatId,
            @Param("uploadedByUserId") BigInteger uploadedByUserId,
            @Param("status") String status,
            Pageable pageable
    );

}