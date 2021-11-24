package com.centram.core.repository;


import com.centram.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, BigInteger> {
    @Query("select r from Role r where upper(r.name) in (:roles)")
    List<Role> getByRoleNames(@Param("roles") List<String> roles);
}
