package com.centram.core.repository;


import com.centram.domain.Action;
import com.centram.domain.Module;
import com.centram.domain.Permission;
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
public interface PermissionRepository extends JpaRepository<Permission, BigInteger> {

    @Modifying
    @Query(value = "insert into permission (role_id,module_id,action_id) VALUES (:roleId,:moduleId,:actionId)", nativeQuery = true)
    void savePermission(@Param("roleId") BigInteger roleId, @Param("moduleId") BigInteger moduleId, @Param("actionId") BigInteger actionId);

    @Modifying
    @Query("delete from Permission p where p.role.id = (:roleId) and p.module.id in (:moduleIds)")
    void deletePermissionByRoleAndMoules(@Param("roleId") BigInteger roleId, @Param("moduleIds") List<BigInteger> moduleIds);

    @Query("select p from Permission p where p.role.id = (:roleId)")
    List<Permission> getPermissionByRoleId(@Param("roleId") BigInteger roleId);

    @Query("select p from Permission p where p.role.id in (:roleIds)")
    Page getPermissionByRoleIds(@Param("roleIds") List<BigInteger> roleIds, Pageable pageable);

    @Query("select p.role.id from Permission p where p.module.id in (:moduleIds) and p.action.name = (:actionName)")
    List<BigInteger> getRoleIdsByModuleAndAction(@Param("moduleIds") List<BigInteger> moduleIds, @Param("actionName") String actionName);

    @Query("select p from Permission p where p.role.name in (:roleNames)")
    List<Permission> getPermissionByRoleNames(@Param("roleNames") List<String> roleNames);

    @Query("select distinct(p.module) from Permission p where p.role.id = (:roleId)")
    List<Module> getModulesByRole(@Param("roleId") BigInteger roleId);

    @Query("select p.action from Permission p where p.role.id = (:roleId) and p.module.id = (:moduleId)")
    List<Action> getActionsByRoleAndModule(@Param("roleId") BigInteger roleId, @Param("moduleId") BigInteger moduleId);

}