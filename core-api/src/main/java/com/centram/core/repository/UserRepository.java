package com.centram.core.repository;


import com.centram.domain.User;
import com.centram.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, BigInteger> {

    @Modifying
    @Query("update User set status = (:status) where id in (:userIds)")
    Integer updateStatus(@Param("status") Status status, @Param("userIds") List<BigInteger> userIds);

    @Modifying
    @Query("update User set password = (:password) where id = (:userId)")
    Integer changePassword(@Param("password") String password, @Param("userId") BigInteger userId);

    @Modifying
    @Query("update User set password = (:password) where id = (:userId)")
    Integer updatePassword(@Param("password") String password, @Param("userId") BigInteger userId);

    @Query("select u from User u where u.email = (:email) and u.status = 1")
    User getUserByEmail(@Param("email") String email);

    @Query("select u from User u where u.id in (:ids)")
    Page getUserByIds(@Param("ids") List<BigInteger> ids, Pageable pageable);

    @Query("select u from User u where u.id = (:id)")
    User getUserById(@Param("id") BigInteger id);

    @Query("select u from User u where u.organisation.id = (:organisationId)")
    Page<User> getUsers(@Param("organisationId") BigInteger organisationId, Pageable pageable);

    @Query("select u from User u where u.organisation.id is null")
    Page<User> getAppUsers(Pageable pageable);

}