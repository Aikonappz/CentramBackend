package com.centram.core.repository;


import com.centram.common.vo.CategoryLocationVO;
import com.centram.domain.Module;
import com.centram.domain.enumarator.LicenseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, BigInteger> {

    @Query("select m from Module m where 1 = 1 and status = 1 and " +
            " ( " +
            "   ((:licenseType) = 'ALL' and m.licenseType in (0,1,2)) " +
            "   OR " +
            "   ((:licenseType) = 'INCIDENT' and m.licenseType in (1)) " +
            "   OR " +
            "   ((:licenseType) = 'ASSET' and m.licenseType in (2)) " +
            " ) "
    )
    Page<Module> findAll(@Param("licenseType") String licenseType, Pageable pageable);

    @Query("select m from Module m where m.appModule = false and m.parentModuleId is null")
    List<Module> getCategories();

    @Query("select m from Module m where m.appModule = false and m.parentModuleId = (:parentModuleId)")
    List<Module> getSubCategories(@Param("parentModuleId") BigInteger parentModuleId);

    Module findByLicenseTypeAndCustomerModuleNameIgnoreCase(@Param("licenseType") LicenseType licenseType, @Param("customerModuleName") String customerModuleName);

    @Query("select m from Module m where m.parentModuleId = (:parentModuleId) and m.customerModuleName = (:customerModuleName)")
    Module getSubModuleByCustomerModuleNameAndParentModuleId(@Param("parentModuleId") BigInteger parentModuleId, @Param("customerModuleName") String customerModuleName);

    @Query(value = "select new com.centram.common.vo.CategoryLocationVO( " +
            " m.id, " +
            " m.name, " +
            " sm.id, " +
            " sm.name," +
            " l.id, " +
            " l.name " +
            " ) " +
            " from Module m " +
            " join Module sm on (m.id=sm.parentModuleId and sm.appModule = false and sm.status = 1) " +
            " join Location l on (l.organisation.id = (:organisationId) and l.status = 1) " +
            " where m.appModule = false and m.status = 1",
            nativeQuery = false
    )
    List<CategoryLocationVO> getCategorySubCategoriesAndLocation(@Param("organisationId") BigInteger organisationId);
}
