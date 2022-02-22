package com.centram.core.repository;


import com.centram.domain.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AssetRepository extends JpaRepository<Asset, BigInteger> {
    @Query(value = "select a from Asset a where a.organisation.id = (:organisationId) and " +
            " ( " +
            "   ((:serialNo) is not null and a.serialNo = (:serialNo)) " +
            "   OR " +
            "   ((:serialNo) is null) " +
            " ) and " +
            " ( " +
            "   ((:productCategory) <> -1 and a.productCategory = (:productCategory)) " +
            "   OR " +
            "   ((:productCategory) = -1) " +
            " ) and " +
            " ( " +
            "   ((:assetType) <> -1 and a.assetType = (:assetType)) " +
            "   OR " +
            "   ((:assetType) = -1) " +
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
            @Param("productCategory") Integer productCategory,
            @Param("assetType") Integer assetType,
            @Param("modelNo") String modelNo,
            @Param("serialNo") String serialNo,
            @Param("assetAvailable") Integer assetAvailable,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );
}