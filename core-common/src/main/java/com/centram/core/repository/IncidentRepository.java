package com.centram.core.repository;


import com.centram.common.vo.CategoryAdminDashboardVO;
import com.centram.common.vo.IncidentPriorityVO;
import com.centram.common.vo.IncidentStatusVO;
import com.centram.common.vo.UserDashboardVO;
import com.centram.domain.Incident;
import com.centram.domain.enumarator.IncidentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface IncidentRepository extends PagingAndSortingRepository<Incident, BigInteger> {

    @Modifying
    @Query("update Incident set status = (:status), assignedUser.id = (:userId), modifiedDate = (:modifiedDate) where id in (:ids)")
    Integer assignIncidents(@Param("status") IncidentStatus status, @Param("userId") BigInteger userId, @Param("modifiedDate") LocalDateTime modifiedDate, @Param("ids") List<BigInteger> ids);

    @Modifying
    @Query("update Incident set status = (:status), modifiedDate = (:modifiedDate) where id in (:ids)")
    Integer changeStatus(@Param("status") IncidentStatus status, @Param("modifiedDate") LocalDateTime modifiedDate, @Param("ids") List<BigInteger> ids);

    @Modifying
    @Query("update Incident set status = (:status), assignedUser = null, modifiedDate = (:modifiedDate) where id in (:ids)")
    Integer reopenIncident(
            @Param("status") IncidentStatus status,
            @Param("modifiedDate") LocalDateTime modifiedDate,
            @Param("ids") List<BigInteger> ids
    );

    @Query("select i from Incident i where i.raisedUser.id = (:raisedUserId) and " +
            " ( " +
            "   ((:status) <> 9 and i.status = (:status)) " +
            "   OR " +
            "   ((:status) = 9) " +
            " ) and " +
            " ( " +
            "   ((:incidentNo) is not null and upper(i.incidentNo) like (:incidentNo)) " +
            "   OR " +
            "   ((:incidentNo) is null) " +
            " ) and " +
            " ( " +
            "   ((:title) is not null and upper(i.title) like (:title)) " +
            "   OR " +
            "   ((:title) is null) " +
            " ) "
    )
    Page<Incident> getIncidents(
            @Param("raisedUserId") BigInteger raisedUserId,
            @Param("incidentNo") String incidentNo,
            @Param("title") String title,
            @Param("status") Integer status,
            Pageable pageable
    );

    @Query("select i from Incident i where i.organisation.id = (:organisationId) and i.moduleId in (:modSubModIds) and i.subModuleId in (:modSubModIds) and " +
            " ( " +
            "   ((:status) <> 9 and i.status = (:status)) " +
            "   OR " +
            "   ((:status) = 9) " +
            " ) and " +
            " ( " +
            "   ((:moduleId) is not null and i.moduleId = (:moduleId)) " +
            "   OR " +
            "   ((:moduleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:subModuleId) is not null and i.subModuleId = (:subModuleId)) " +
            "   OR " +
            "   ((:subModuleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:priorityId) is not null and i.priority.id = (:priorityId)) " +
            "   OR " +
            "   ((:priorityId) is null) " +
            " ) and " +
            " ( " +
            "   ((:assignedUserId) is not null and i.assignedUser.id = (:assignedUserId)) " +
            "   OR " +
            "   ((:assignedUserId) is null) " +
            " ) and " +
            " ( " +
            "   ((:incidentNo) is not null and upper(i.incidentNo) like (:incidentNo)) " +
            "   OR " +
            "   ((:incidentNo) is null) " +
            " ) and " +
            " ( " +
            "   ((:title) is not null and upper(i.title) like (:title)) " +
            "   OR " +
            "   ((:title) is null) " +
            " ) "
    )
    Page<Incident> getIncomingIncidents(
            @Param("incidentNo") String incidentNo,
            @Param("moduleId") BigInteger moduleId,
            @Param("subModuleId") BigInteger subModuleId,
            @Param("priorityId") BigInteger priorityId,
            @Param("assignedUserId") BigInteger assignedUserId,
            @Param("modSubModIds") List<BigInteger> modSubModIds,
            @Param("title") String title,
            @Param("status") Integer status,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );

    @Query(value = "select * from incident i where i.organisation_id = (:organisationId) and (i.created_date between (:start) and (:end)) and " +
            " ( " +
            "   ((:modFilter) = true and i.module_id in (:modSubModIds) and i.sub_module_id in (:modSubModIds)) " +
            "   OR " +
            "   ((:modFilter) = false) " +
            " ) and " +
            " ( " +
            "   ((:status) <> 9 and i.status = (:status)) " +
            "   OR " +
            "   ((:status) = 9) " +
            " ) and " +
            " ( " +
            "   ((:moduleId) is not null and i.module_id = (:moduleId)) " +
            "   OR " +
            "   ((:moduleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:subModuleId) is not null and i.sub_module_id = (:subModuleId)) " +
            "   OR " +
            "   ((:subModuleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:raisedUserId) is not null and i.raised_user_id = (:raisedUserId)) " +
            "   OR " +
            "   ((:raisedUserId) is null) " +
            " ) and " +
            " ( " +
            "   ((:assignedUserId) is not null and i.assigned_user_id = (:assignedUserId)) " +
            "   OR " +
            "   ((:assignedUserId) is null) " +
            " ) and " +
            " ( " +
            "   ((:priorityId) is not null and i.priority_id = (:priorityId)) " +
            "   OR " +
            "   ((:priorityId) is null) " +
            " ) " +
            " and " +
            " ( " +
            "   ((:agingFilter) = '>=5' and datediff(SYSDATE() ,i.created_date) between 5 and 9) " +
            "   OR " +
            "   ((:agingFilter) = '>=10' and datediff(SYSDATE() ,i.created_date) between 10 and 19) " +
            "   OR " +
            "   ((:agingFilter) = '>=20' and datediff(SYSDATE() ,i.created_date) between 20 and 29) " +
            "   OR " +
            "   ((:agingFilter) = '>=30' and datediff(SYSDATE() ,i.created_date) between 30 and 59) " +
            "   OR " +
            "   ((:agingFilter) = '>60' and datediff(SYSDATE() ,i.created_date) > 60) " +
            "   OR " +
            "   ((:agingFilter) is null) " +
            " )",
            nativeQuery = true)
    Page<Incident> incidentReport(
            @Param("moduleId") BigInteger moduleId,
            @Param("subModuleId") BigInteger subModuleId,
            @Param("priorityId") BigInteger priorityId,
            @Param("raisedUserId") BigInteger raisedUserId,
            @Param("assignedUserId") BigInteger assignedUserId,
            @Param("status") Integer status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("modFilter") Boolean modFilter,
            @Param("modSubModIds") List<BigInteger> modSubModIds,
            @Param("agingFilter") String agingFilter,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );

    @Query("select i from Incident i where " +
            " (i.escalation1At is not null or i.escalation2At is not null) and " +
            " i.organisation.id = (:organisationId) and (i.createdDate between (:start) and (:end)) and " +
            " ( " +
            "   ((:modFilter) = true and i.moduleId in (:modSubModIds) and i.subModuleId in (:modSubModIds)) " +
            "   OR " +
            "   ((:modFilter) = false) " +
            " ) and " +
            " ( " +
            "   ((:status) <> 9 and i.status = (:status)) " +
            "   OR " +
            "   ((:status) = 9) " +
            " ) and " +
            " ( " +
            "   ((:moduleId) is not null and i.moduleId = (:moduleId)) " +
            "   OR " +
            "   ((:moduleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:subModuleId) is not null and i.subModuleId = (:subModuleId)) " +
            "   OR " +
            "   ((:subModuleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:priorityId) is not null and i.priority.id = (:priorityId)) " +
            "   OR " +
            "   ((:priorityId) is null) " +
            " ) "
    )
    Page<Incident> incidentEscalationReport(
            @Param("moduleId") BigInteger moduleId,
            @Param("subModuleId") BigInteger subModuleId,
            @Param("priorityId") BigInteger priorityId,
            @Param("status") Integer status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("modFilter") Boolean modFilter,
            @Param("modSubModIds") List<BigInteger> modSubModIds,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );

    @Query("select i from Incident i where " +
            " (i.reopenedAt is not null or i.reOpened = true) and " +
            " i.organisation.id = (:organisationId) and (i.createdDate between (:start) and (:end)) and " +
            " ( " +
            "   ((:modFilter) = true and i.moduleId in (:modSubModIds) and i.subModuleId in (:modSubModIds)) " +
            "   OR " +
            "   ((:modFilter) = false) " +
            " ) and " +
            " ( " +
            "   ((:status) <> 9 and i.status = (:status)) " +
            "   OR " +
            "   ((:status) = 9) " +
            " ) and " +
            " ( " +
            "   ((:moduleId) is not null and i.moduleId = (:moduleId)) " +
            "   OR " +
            "   ((:moduleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:subModuleId) is not null and i.subModuleId = (:subModuleId)) " +
            "   OR " +
            "   ((:subModuleId) is null) " +
            " ) and " +
            " ( " +
            "   ((:priorityId) is not null and i.priority.id = (:priorityId)) " +
            "   OR " +
            "   ((:priorityId) is null) " +
            " ) "
    )
    Page<Incident> incidentReopenReport(
            @Param("moduleId") BigInteger moduleId,
            @Param("subModuleId") BigInteger subModuleId,
            @Param("priorityId") BigInteger priorityId,
            @Param("status") Integer status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("modFilter") Boolean modFilter,
            @Param("modSubModIds") List<BigInteger> modSubModIds,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );

    @Query("select i from Incident i where i.organisation.id = (:organisationId) and i.status in (:statuses) order by i.id asc")
    List<Incident> getIncidentsByStatus(@Param("organisationId") BigInteger organisationId, @Param("statuses") List<IncidentStatus> statuses);

    @Query("select i from Incident i where i.status in (:statuses) order by i.id asc")
    List<Incident> getAllIncidentsByStatus(@Param("statuses") List<IncidentStatus> statuses);

    @Query("select i from Incident i where i.status in (:statuses) and i.organisation.id = (:organisationId) order by i.id asc")
    List<Incident> getIncidentsByOrganisationAndStatus(@Param("organisationId") BigInteger organisationId, @Param("statuses") List<IncidentStatus> statuses);

    @Query("select i from Incident i where i.moduleId = (:categoryId) and i.subModuleId = (:subCategoryId) and i.status in (:statuses) and i.organisation.id = (:organisationId) order by i.id asc")
    List<Incident> getIncidentsByOrganisationAndStatusAndCategoryAndSubCategory(
            @Param("categoryId") BigInteger categoryId,
            @Param("subCategoryId") BigInteger subCategoryId,
            @Param("organisationId") BigInteger organisationId,
            @Param("statuses") List<IncidentStatus> statuses
    );

    @Query(value = " select " +
            " sum(case when i.status = 0 then 1 else 0 end ) as openIncidents, " +
            " sum(case when i.status = 1 then 1 else 0 end ) as assignedIncidents, " +
            " sum(case when i.status = 4 then 1 else 0 end ) as closedIncidents " +
            " from " +
            " incident i " +
            " WHERE " +
            " i.created_date BETWEEN (:start) and (:end) " +
            " and " +
            " i.raised_user_id = (:userId) ", nativeQuery = true)
    UserDashboardVO userDashboardData(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("userId") BigInteger userId
    );

    @Query(value = "select " +
            " m.id as moduleId, " +
            " m.name as status, " +
            " CONCAT(UPPER(SUBSTRING(m.customer_module_name,1,1)),LOWER(SUBSTRING(m.customer_module_name,2))) as statusName, " +
            " sum(case when i.id is not null and u.id is not null then 1 else 0 end) as count" +
            " from " +
            " module m " +
            " left outer join incident i on (i.module_id = m.id and i.organisation_id = (:organisationId) and i.created_date BETWEEN (:start) and (:end) ) " +
            " left outer join user u on " +
            "  ( " +
            "    ((:userType) = 'USER' and u.id = i.raised_user_id and u.id = (:userId)) " +
            "    OR " +
            "    ((:userType) = 'AGENT' and u.id = i.assigned_user_id and u.id = (:userId)) " +
            "    OR " +
            "    ((:userType) = 'AGENT_LEAD' and u.id = i.raised_user_id ) " +
            "    OR " +
            "    ((:userType) = 'AGENT_MANAGER' and u.id = i.raised_user_id ) " +
            "    OR " +
            "    ((:userType) = 'CATEGORY_ADMIN' and u.id = i.raised_user_id ) " +
            "    OR " +
            "    ((:userType) = 'ORG_ADMIN' and u.id = i.raised_user_id ) " +
            "  ) " +
            " where m.app_module = 0 and m.parent_module_id is null and " +
            "  ( " +
            "    ((:roleFilter) = true and m.id in (:userModules)) " +
            "    OR " +
            "    ((:roleFilter) = false) " +
            "  ) " +
            " group by m.id, m.name, CONCAT(UPPER(SUBSTRING(m.customer_module_name,1,1)),LOWER(SUBSTRING(m.customer_module_name,2))) " +
            " order by 2 asc ", nativeQuery = true)
    Set<IncidentStatusVO> orgStatusWiseIncidentDashboardData(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("roleFilter") Boolean roleFilter,
            @Param("userModules") List<BigInteger> userModules,
            @Param("organisationId") BigInteger organisationId,
            @Param("userType") String userType,
            @Param("userId") BigInteger userId
    );

    @Query(value = " select " +
            " p.id as priorityId," +
            " p.name as priority, " +
            " sum(case when i.id is not null and u.id is not null then 1 else 0 end) as count " +
            " FROM " +
            " priority p " +
            " left outer join incident i on (" +
            "   p.id = i.priority_id and i.organisation_id =  (:organisationId) and i.created_date BETWEEN (:start) and (:end) and " +
            "  ( " +
            "    ((:roleFilter) = true and i.module_id in (:userModules) and i.sub_module_id in (:userSubModules)) " +
            "    OR " +
            "    ((:roleFilter) = false) " +
            "  ) " +
            " ) " +
            " left outer join user u on " +
            "  ( " +
            "    ((:userType) = 'USER' and u.id = i.raised_user_id and (((:userFilter) = true and u.id = (:userId)) or (:userFilter) = false)) " +
            "    OR " +
            "    ((:userType) = 'AGENT' and u.id = i.assigned_user_id and (((:userFilter) = true and u.id = (:userId)) or (:userFilter) = false)) " +
            "    OR " +
            "    ((:userType) = 'AGENT_LEAD' and u.id = i.raised_user_id ) " +
            "    OR " +
            "    ((:userType) = 'AGENT_MANAGER' and u.id = i.raised_user_id ) " +
            "    OR " +
            "    ((:userType) = 'CATEGORY_ADMIN' and u.id = i.raised_user_id ) " +
            "    OR " +
            "    ((:userType) = 'ORG_ADMIN' and u.id = i.raised_user_id ) " +
            "  ) " +
            " where 1 = 1 and p.organisation_id =  (:organisationId) " +
            " group by p.id, p.name " +
            " order by 1 asc ", nativeQuery = true)
    Set<IncidentPriorityVO> orgPriorityWiseIncidentDashboardData(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("roleFilter") Boolean roleFilter,
            @Param("userModules") List<BigInteger> userModules,
            @Param("userSubModules") List<BigInteger> userSubModules,
            @Param("organisationId") BigInteger organisationId,
            @Param("userType") String userType,
            @Param("userFilter") Boolean userFilter,
            @Param("userId") BigInteger userId
    );

    @Query(nativeQuery = true)
    CategoryAdminDashboardVO agingWiseIncidentDashboardData(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("roleFilter") Boolean roleFilter,
            @Param("userModules") List<BigInteger> userModules,
            @Param("userSubModules") List<BigInteger> userSubModules,
            @Param("organisationId") BigInteger organisationId
    );
}