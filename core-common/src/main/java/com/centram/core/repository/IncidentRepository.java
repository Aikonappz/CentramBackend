package com.centram.core.repository;


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
            "   ((:status) <> 12 and i.status = (:status)) " +
            "   OR " +
            "   ((:status) = 12) " +
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
            "   ((:status) <> 12 and i.status = (:status)) " +
            "   OR " +
            "   ((:status) = 12) " +
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

    @Query("select i from Incident i where i.status in (:statuses)")
    List<Incident> getNonBlockedIncidents(@Param("statuses") List<IncidentStatus> statuses);


}