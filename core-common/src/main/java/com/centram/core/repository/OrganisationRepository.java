package com.centram.core.repository;

import com.centram.common.vo.AdminDashboardVO;
import com.centram.domain.Organisation;
import com.centram.domain.Setting;
import com.centram.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;


public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, BigInteger> {
    @Modifying
    @Query("update Organisation set status = (:status), modifiedDate = (:modifiedDate) where id in (:organisationIds)")
    Integer updateStatus(@Param("status") Status status, @Param("modifiedDate") LocalDateTime modifiedDate, @Param("organisationIds") List<BigInteger> organisationId);

    @Modifying
    @Query("update Organisation set setting = (:setting), modifiedDate = (:modifiedDate) where id = (:organisationId)")
    Integer updateSetting(@Param("setting") Setting setting, @Param("modifiedDate") LocalDateTime modifiedDate, @Param("organisationId") BigInteger organisationId);

    @Query("select o from Organisation o where o.centramKey = (:centramKey) and o.centramPass = (:centramPass)")
    Organisation getOrganisationByApiUserKeyAndUserPassword(@Param("centramKey") String centramKey, @Param("centramPass") String centramPass);

    //@Query("select o from Organisation o where upper(o.name) like %:name%")
    //Page<Organisation> findByName(@Param("name") String name, Pageable pageable);

    //@Query("select o from Organisation o where upper(o.name) like %:name% and o.status = (:status)")
    //Page<Organisation> findByNameAndStatus(@Param("name") String name, @Param("status") Status status, Pageable pageable);

    @Query("select o from Organisation o where 1 = 1 and " +
            " ( " +
            "   ((:status) <> 2 and o.status = (:status)) " +
            "   OR  " +
            "   ((:status) = 2) " +
            " ) and " +
            " ( " +
            "   ((:licenseType) <> 0 and o.licenseType = (:licenseType)) " +
            "   OR  " +
            "   ((:licenseType) = 0) " +
            " ) and " +
            " ( " +
            "   ((:name) is not null and UPPER(o.name) like (:name)) " +
            "   OR " +
            "   ((:name) is null) " +
            " ) "
    )
    Page<Organisation> findAll(
            @Param("name") String name,
            @Param("status") Integer status,
            @Param("licenseType") Integer licenseType,
            Pageable pageable
    );

    @Query("select o from Organisation o where o.status = 1")
    List<Organisation> findAll();

    @Query(value = "select " +
            " sum(1) as totalCompanies, " +
            " sum(case when status = 0 then 0 else 1 end) as activeCompanies, " +
            " sum(case when status = 0 then 1 else 0 end) as inactiveCompanies, " +
            " sum(case when license_type = 0 then 1 else 0 end) as allLicenceTypeCompanies, " +
            " sum(case when license_type = 1 then 1 else 0 end) as incidentLicenceTypeCompanies, " +
            " sum(case when license_type = 2 then 1 else 0 end) as assetLicenceTypeCompanies " +
            " from organisation ", nativeQuery = true)
    AdminDashboardVO appAdminDashboardData();
}