package com.erp.auth.repository;


import com.erp.domain.User;
import com.erp.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {

    @Modifying
    @Query("update User set status = (:status) where id = (:userId)")
    Integer updateStatus(@Param("status") Status status, @Param("userId") BigInteger userId);

    @Query("select u from User u where u.userName = (:userName)")
    User getUserByUserName(@Param("userName") String userName);

    @Query("select u from User u where u.id in (:ids)")
    Page getUserByIds(@Param("ids") List<BigInteger> ids, Pageable pageable);

}