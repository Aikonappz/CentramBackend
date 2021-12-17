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
import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, BigInteger> {

    @Query("select u from User u where upper(u.employeeId) = upper((:employeeId)) and u.organisation.id = (:organisationId)")
    User getUserByEmployeeId(@Param("employeeId") String employeeId, @Param("organisationId") BigInteger organisationId);

    @Query("select u from User u where upper(u.email) = upper((:email)) and u.organisation.id = (:organisationId)")
    User getUserByEmail(@Param("email") String email, @Param("organisationId") BigInteger organisationId);

    @Query("select u from User u where u.email in (:emails) and u.organisation.id = (:organisationId)")
    List<User> getUsersByEmails(@Param("emails") List<String> emails, @Param("organisationId") BigInteger organisationId);

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

    /*select roles from `user` u where organisation_id = 1
    and CONCAT(",", `roles`, ",") REGEXP ",(6|10),"
    and find_in_set('15',roles) <> 0*/

    @Query(value = "select * from user u where u.organisation_id = (:organisationId) and CONCAT(',',u.roles,',') REGEXP (:roleExp)", nativeQuery = true)
    List<User> getUsersByRoleIds(@Param("roleExp") String roleExp, @Param("organisationId") BigInteger organisationId);

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
    );

    //@Query("select u from User u where u.organisation.id = (:organisationId)")
    //Page<User> getUsers(@Param("organisationId") BigInteger organisationId, Pageable pageable);

    //@Query("select u from User u where u.organisation.id is null")
    //Page<User> getAppUsers(Pageable pageable);

}