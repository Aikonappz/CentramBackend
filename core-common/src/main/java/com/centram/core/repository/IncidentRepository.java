package com.centram.core.repository;


import com.centram.common.vo.IncidentAgingVO;
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

    @Query("select i from Incident i where i.moduleId in (:modSubModIds) and i.subModuleId in (:modSubModIds) and " +
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
            Pageable pageable
    );

    @Query("select i from Incident i where i.status in (:statuses) order by i.id asc")
    List<Incident> getIncidentsByStatus(@Param("statuses") List<IncidentStatus> statuses);

    @Query("select i from Incident i join i.raisedUser ru join ru.organisation o where i.status in (:statuses) and o.id = (:organisationId) order by i.id asc")
    List<Incident> getIncidentsByOrganisationAndStatus(@Param("organisationId") BigInteger organisationId, @Param("statuses") List<IncidentStatus> statuses);

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
            " m.name as status, " +
            " CONCAT(UPPER(SUBSTRING(m.customer_module_name,1,1)),LOWER(SUBSTRING(m.customer_module_name,2))) as statusName, " +
            " COUNT(1) as count" +
            " from " +
            " incident i " +
            " join module m on (m.app_module = 0 and m.parent_module_id is null and m.id = i.module_id) " +
            " join user u on (u.id = i.raised_user_id) " +
            " where u.organisation_id =  (:organisationId) " +
            " group by m.name, CONCAT(UPPER(SUBSTRING(m.customer_module_name,1,1)),LOWER(SUBSTRING(m.customer_module_name,2))) " +
            " order by 1 asc ", nativeQuery = true)
    Set<IncidentStatusVO> orgStatusWiseIncidentDashboardData(
            @Param("organisationId") BigInteger organisationId
    );

    @Query(value = " select " +
            " p.name as priority, " +
            " sum(1) as count " +
            " FROM " +
            " incident i " +
            " join priority p on (p.id = i.priority_id) " +
            " where p.organisation_id =  (:organisationId) " +
            "group by p.name " +
            " order by 1 asc ", nativeQuery = true)
    Set<IncidentPriorityVO> orgPriorityWiseIncidentDashboardData(
            @Param("organisationId") BigInteger organisationId
    );

    @Query(value = " SELECT " +
            " sum(case when date_differnce >= 5 && date_differnce < 10 then 1 else 0 end) as aging5, " +
            " sum(case when date_differnce >= 10 && date_differnce < 20 then 1 else 0 end) as aging10, " +
            " sum(case when date_differnce >= 20 && date_differnce < 30 then 1 else 0 end) as aging20, " +
            " sum(case when date_differnce >= 30 && date_differnce < 60 then 1 else 0 end) as aging30, " +
            " sum(case when date_differnce > 60 then 1 else 0 end) as aging60 " +
            " from " +
            " ( " +
            "  select datediff(SYSDATE() ,i.created_date) as date_differnce from  incident i " +
            "  join user u on (u.id = i.raised_user_id) " +
            "  where u.organisation_id = (:organisationId) " +
            " ) tab ", nativeQuery = true)
    IncidentAgingVO orgAgingWiseIncidentDashboardData(
            @Param("organisationId") BigInteger organisationId
    );
}