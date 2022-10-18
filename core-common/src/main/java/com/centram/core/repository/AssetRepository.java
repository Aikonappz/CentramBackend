package com.centram.core.repository;


import com.centram.domain.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, BigInteger> {
    @Query(value = "select a from Asset a where a.organisation.id = (:organisationId) and " +
            " ( " +
            "   ((:serialNo) is not null and a.serialNo = (:serialNo)) " +
            "   OR " +
            "   ((:serialNo) is null) " +
            " ) and " +
            " ( " +
            "   ((:productCategory) is not null and a.moduleId = (:productCategory)) " +
            "   OR " +
            "   ((:productCategory) is null) " +
            " ) and " +
            " ( " +
            "   ((:assetType) is not null and a.subModuleId = (:assetType)) " +
            "   OR " +
            "   ((:assetType) is null) " +
            " ) and " +
            " ( " +
            "   ((:modelNo) is not null and a.modelNo = (:modelNo)) " +
            "   OR " +
            "   ((:modelNo) is null) " +
            " ) and " +
            " ( " +
            "   ((:serialNo) is not null and a.serialNo = (:serialNo)) " +
            "   OR " +
            "   ((:serialNo) is null) " +
            " ) and " +
            " ( " +
            "   ((:assetAvailable) <> -1 and a.isAvailable = (:assetAvailable)) " +
            "   OR " +
            "   ((:assetAvailable) = -1) " +
            " ) "
    )
    Page<Asset> findAll(
            @Param("productCategory") BigInteger productCategory,
            @Param("assetType") BigInteger assetType,
            @Param("modelNo") String modelNo,
            @Param("serialNo") String serialNo,
            @Param("assetAvailable") Integer assetAvailable,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );

    @Query(value = "select a from Asset a where a.organisation.id = (:organisationId) and " +
            " ( " +
            "   ((:productCategory) is not null and a.moduleId = (:productCategory)) " +
            "   OR " +
            "   ((:productCategory) is null) " +
            " ) and " +
            " ( " +
            "   ((:assetType) is not null and a.subModuleId = (:assetType)) " +
            "   OR " +
            "   ((:assetType) is null) " +
            " ) and " +
            " ( " +
            "   isDepartment = true and a.department.id = (:departmentId) and isLocation = true and a.raisedForLocation.id = (:locationId) " +
            "   OR " +
            "   isDepartment = true and a.department.id = (:departmentId) and isLocation = false " +
            "   OR " +
            "   isDepartment = false and a.location.id = (:locationId)  " +
            " ) and a.isAvailable = true "
    )
    Page<Asset> findAll(
            @Param("productCategory") BigInteger productCategory,
            @Param("assetType") BigInteger assetType,
            @Param("locationId") BigInteger locationId,
            @Param("departmentId") BigInteger departmentId,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );

    @Query(value = "select a from Asset a where a.organisation.id = (:organisationId) and a.warrantyExpirationMessageSent = false and a.warrantyExpiredMessageSent = false")
    List<Asset> findAll(@Param("organisationId") BigInteger organisationId);
}