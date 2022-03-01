package com.centram.core.repository;


import com.centram.domain.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, BigInteger> {
    @Query("select r from Role r where UPPER(r.name) in (:roles)")
    List<Role> getByRoleNames(@Param("roles") List<String> roles);

    Optional<Role> findByName(@Param("name") String name);

    Optional<Role> findByDisplayName(@Param("displayName") String displayName);
}