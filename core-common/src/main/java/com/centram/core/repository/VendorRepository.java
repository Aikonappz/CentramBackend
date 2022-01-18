package com.centram.core.repository;


import com.centram.common.vo.OrgAdminDashboardVO;
import com.centram.domain.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, BigInteger> {
    @Query("select v from Vendor v join v.organisation org where upper(v.name) = upper((:name)) and org.id = (:organisationId)")
    Vendor getByName(@Param("name") String name, @Param("organisationId") BigInteger organisationId);

    @Query("select v from Vendor v join v.vendorModules vm join v.organisation org where vm.moduleId = (:moduleId) and vm.subModuleId = (:subModuleId) and org.id = (:organisationId)")
    List<Vendor> getByModuleIdAndSubModuleId(@Param("moduleId") BigInteger moduleId, @Param("subModuleId") BigInteger subModuleId, @Param("organisationId") BigInteger organisationId);

    @Query("select v from Vendor v join v.organisation org where org.id = (:organisationId)")
    Page getByOrganisation(@Param("organisationId") BigInteger organisationId, @Param("pageable") Pageable pageable);

    @Query(value = "select " +
            " new com.centram.common.vo.OrgAdminDashboardVO(" +
            " sum(case when v.inHouse = true and v.organisation.id =  (:organisationId) then 1 else 0 end), " +
            " sum(case when v.inHouse = false and v.organisation.id =  (:organisationId) then 1 else 0 end) " +
            " ) " +
            " from " +
            " Vendor v ", nativeQuery = false)
    OrgAdminDashboardVO orgAdminVendorDashboardData(@Param("organisationId") BigInteger organisationId);
}