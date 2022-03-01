package com.centram.core.repository;


import com.centram.domain.User;
import com.centram.domain.enumarator.Status;
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
public interface UserRepository extends PagingAndSortingRepository<User, BigInteger> {

    @Query(value = "select u from User u where UPPER(u.employeeId) = UPPER((:employeeId)) and " +
            " ( " +
            "   ((:organisationId) is not null and u.organisation.id = (:organisationId)) " +
            "   OR " +
            "   ((:organisationId) is null) " +
            " ) "
    )
    User getUserByEmployeeId(@Param("employeeId") String employeeId, @Param("organisationId") BigInteger organisationId);

    @Query("select u from User u where UPPER(u.email) = UPPER((:email)) and u.organisation.id = (:organisationId)")
    User getUserByEmail(@Param("email") String email, @Param("organisationId") BigInteger organisationId);

    @Query("select u from User u where u.email in (:emails) and u.organisation.id = (:organisationId)")
    List<User> getUsersByEmails(@Param("emails") List<String> emails, @Param("organisationId") BigInteger organisationId);

    @Modifying
    @Query("update User set status = (:status), modifiedDate = (:modifiedDate) where id in (:userIds)")
    Integer updateStatus(@Param("status") Status status, @Param("modifiedDate") LocalDateTime modifiedDate, @Param("userIds") List<BigInteger> userIds);

    @Modifying
    @Query("update User set password = (:password), modifiedDate = (:modifiedDate) where id = (:userId)")
    Integer updatePassword(@Param("password") String password, @Param("modifiedDate") LocalDateTime modifiedDate, @Param("userId") BigInteger userId);

    @Query("select u from User u left join u.organisation o where u.email = (:email)")
    User getUserByEmail(@Param("email") String email);

    @Query("select u from User u where u.id in (:ids)")
    Page getUserByIds(@Param("ids") List<BigInteger> ids, Pageable pageable);

    /*select roles from `user` u where organisation_id = 1
    and CONCAT(",", `roles`, ",") REGEXP ",(6|10),"
    and find_in_set('15',roles) <> 0*/

    @Query(value = "select * from user u where 1 = 1 and u.organisation_id is null and CONCAT(',',u.roles,',') REGEXP (:roleExp)", nativeQuery = true)
    List<User> getAdminUsers(@Param("roleExp") String roleExp);

    @Query(value = "select * from user u where u.organisation_id = (:organisationId) and CONCAT(',',u.roles,',') REGEXP (:roleExp)", nativeQuery = true)
    List<User> getUsersByRoleIds(@Param("roleExp") String roleExp, @Param("organisationId") BigInteger organisationId);

    @Query(value = "SELECT u.* FROM user u where " +
            " (select count(r.name) from role r " +
            " where FIND_IN_SET(r.id,u.roles) > 0 " +
            " and r.name REGEXP (:roleExp)) > 0 " +
            " and u.organisation_id = (:organisationId) ", nativeQuery = true)
    List<User> getUsersByRoleNames(@Param("roleExp") String roleExp, @Param("organisationId") BigInteger organisationId);

    @Query(value = "select u.* from user u join vendor v on (v.id=u.vendor_id and v.ticket_allocation_type=1) " +
            " join vendor_module vm on (v.id = vm.vendor_id and vm.module_id = (:moduleId) and vm.sub_module_id = (:subModuleId)) " +
            " where u.location_id = (:locationId) and u.organisation_id = (:organisationId) and CONCAT(',',u.roles,',') REGEXP (:roleExp)", nativeQuery = true)
    List<User> getAgents(
            @Param("moduleId") BigInteger moduleId,
            @Param("subModuleId") BigInteger subModuleId,
            @Param("roleExp") String roleExp,
            @Param("locationId") BigInteger locationId,
            @Param("organisationId") BigInteger organisationId
    );

    @Query("select u from User u where u.id = (:id)")
    User getUserById(@Param("id") BigInteger id);

    @Query(value = "select * from user u where 1 = 1 and " +
            " ( " +
            "   ((:organisationId) is not null and u.organisation_id = (:organisationId)) " +
            "   OR " +
            "   ((:organisationId) is null and u.organisation_id is null) " +
            " ) and " +
            " ( " +
            "   ((:vendorId) is not null and u.vendor_id = (:vendorId)) " +
            "   OR " +
            "   ((:vendorId) is null) " +
            " ) and " +
            " ( " +
            "   ((:status) <> 2 and u.status = (:status)) " +
            "   OR " +
            "   ((:status) = 2) " +
            " ) and " +
            " ( " +
            "   ((:email) is not null and UPPER(u.email) like (:email)) " +
            "   OR " +
            "   ((:email) is null) " +
            " ) and " +
            " ( " +
            "   ((:filterType) = 'USER' and (select count(r.name) from role r where FIND_IN_SET(r.id,u.roles) > 0 and r.name like '%_USER_%') > 0) " +
            "   OR " +
            "   ((:filterType) = 'AGENT' and (select count(r.name) from role r where FIND_IN_SET(r.id,u.roles) > 0 and r.name like '%_AGENT_%') > 0) " +
            "   OR " +
            "   ((:filterType) = 'AGENT_VENDOR' and (select count(r.name) from role r where FIND_IN_SET(r.id,u.roles) > 0 and r.name like '%_AGENT_%') > 0 and u.vendor_id is not null and u.status = 1) " +
            "   OR " +
            "   ((:filterType) is null) " +
            " ) and " +
            " ( " +
            "   ((:employeeId) is not null and UPPER(u.employee_id) like (:employeeId)) " +
            "   OR " +
            "   ((:employeeId) is null)" +
            " ) ", nativeQuery = true
    )
    Page<User> getUsers(
            @Param("organisationId") BigInteger organisationId,
            @Param("email") String email,
            @Param("employeeId") String employeeId,
            @Param("status") Integer status,
            @Param("filterType") String filterType,
            @Param("vendorId") BigInteger vendorId,
            Pageable pageable
    );

    @Query(value = "select " +
            " sum(case when u.vendor_id is null then 1 else 0 end)" +
            " from " +
            " user u " +
            " where " +
            " u.organisation_id =  (:organisationId) and u.status = 1", nativeQuery = true)
    long orgAdminUserDashboardData(@Param("organisationId") BigInteger organisationId);
}