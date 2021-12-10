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
    @Query("update Incident set assignedUser.id = (:userId), modifiedDate = (:modifiedDate) where id in (:ids)")
    Integer assignIncidents(@Param("userId") BigInteger userId, @Param("modifiedDate") LocalDateTime modifiedDate, @Param("ids") List<BigInteger> ids);

    @Modifying
    @Query("update Incident set status = (:status), modifiedDate = (:modifiedDate) where id in (:ids)")
    Integer changeStatus(@Param("status") IncidentStatus status, @Param("modifiedDate") LocalDateTime modifiedDate, @Param("ids") List<BigInteger> ids);

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

    /*@Query("select u from User u where upper(u.employeeId) = upper((:employeeId)) and u.organisation.id = (:organisationId)")
    User getUserByEmployeeId(@Param("employeeId") String employeeId, @Param("organisationId") BigInteger organisationId);

    @Query("select u from User u where upper(u.email) = upper((:email)) and u.organisation.id = (:organisationId)")
    User getUserByEmail(@Param("email") String email, @Param("organisationId") BigInteger organisationId);

    @Modifying
    @Query("update User set status = (:status) where id in (:userIds)")
    Integer updateStatus(@Param("status") Status status, @Param("userIds") List<BigInteger> userIds);

    @Modifying
    @Query("update User set password = (:password) where id = (:userId)")
    Integer changePassword(@Param("password") String password, @Param("userId") BigInteger userId);

    @Modifying
    @Query("update User set password = (:password) where id = (:userId)")
    Integer updatePassword(@Param("password") String password, @Param("userId") BigInteger userId);

    @Query("select u from User u " +
            "left join u.organisation o " +
            "where u.email = (:email) and u.status = 1 " +
            "and (u.organisation.id is null or o.status = 1)")
    User getUserByEmail(@Param("email") String email);

    @Query("select u from User u where u.id in (:ids)")
    Page getUserByIds(@Param("ids") List<BigInteger> ids, Pageable pageable);

    @Query("select u from User u where u.id = (:id)")
    User getUserById(@Param("id") BigInteger id);

    @Query("select u from User u where 1 = 1 and " +
            " ( " +
            "   ((:organisationId) is not null and u.organisation.id = (:organisationId)) " +
            "   OR " +
            "   ((:organisationId) is null and u.organisation.id is null) " +
            " ) and " +
            " ( " +
            "   ((:status) <> 2 and u.status = (:status)) " +
            "   OR " +
            "   ((:status) = 2) " +
            " ) and " +
            " ( " +
            "   ((:email) is not null and upper(u.email) like (:email)) " +
            "   OR " +
            "   ((:email) is null) " +
            " ) and " +
            " ( " +
            "   ((:employeeId) is not null and upper(u.employeeId) like (:employeeId)) " +
            "   OR " +
            "   ((:employeeId) is null)" +
            " )"
    )
    Page<User> getUsers(
            @Param("organisationId") BigInteger organisationId,
            @Param("email") String email,
            @Param("employeeId") String employeeId,
            @Param("status") Integer status,
            Pageable pageable
    );*/

    //@Query("select u from User u where u.organisation.id = (:organisationId)")
    //Page<User> getUsers(@Param("organisationId") BigInteger organisationId, Pageable pageable);

    //@Query("select u from User u where u.organisation.id is null")
    //Page<User> getAppUsers(Pageable pageable);

}