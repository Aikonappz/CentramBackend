package com.erp.auth.repository;


import com.erp.domain.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, BigInteger> {

    @Query("select p from Permission p where p.role.id = (:roleId)")
    Page getPermissionByRoleId(@Param("roleId") BigInteger roleId, Pageable pageable);

    @Query("select p from Permission p where p.role.id in (:roleIds)")
    Page getPermissionByRoleIds(@Param("roleIds") List<BigInteger> roleIds, Pageable pageable);

}