package com.centram.core.repository;


import com.centram.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, BigInteger> {
    @Query("select m from Module m where m.appModule = false and m.parentModuleId is null")
    List<Module> getCategories();
    @Query("select m from Module m where m.appModule = false and m.parentModuleId = (:parentModuleId)")
    List<Module> getSubCategories(@Param("parentModuleId") BigInteger parentModuleId);
}
