package com.centram.core.repository;


import com.centram.domain.AssetRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AssetRequestRepository extends JpaRepository<AssetRequest, BigInteger> {
    @Query("select ar from AssetRequest ar left join ar.asset a where 1 = 1 and " +
            " ( " +
            "   ((:serialNo) is not null and (ar.asset is not null and ar.asset.serialNo = (:serialNo))) " +
            "   OR " +
            "   ((:serialNo) is null) " +
            " ) and " +
            " ( " +
            "   ((:productCategory) <> -1 and ar.productCategory = (:productCategory)) " +
            "   OR " +
            "   ((:productCategory) = -1) " +
            " ) and " +
            " ( " +
            "   ((:assetType) <> -1 and ar.assetType = (:assetType)) " +
            "   OR " +
            "   ((:assetType) = -1) " +
            " ) and " +
            " ( " +
            "   ((:modelNo) is not null and ar.modelNo = (:modelNo)) " +
            "   OR " +
            "   ((:modelNo) is null) " +
            " ) and " +
            " ( " +
            "   ((:approved) <> -1 and ar.approved = (:approved)) " +
            "   OR " +
            "   ((:approved) = -1) " +
            " ) and " +
            " ( " +
            "   ((:allocated) <> -1 and ar.allocated = (:allocated)) " +
            "   OR " +
            "   ((:allocated) = -1) " +
            " ) and " +
            " ( " +
            "   ((:requestFrom) <> -1 and ar.approved = true and ar.approverComment is not null) " +
            "   OR " +
            "   ((:requestFrom) = -1 and ar.user.id = (:userId)) " +
            " ) "
    )
    Page<AssetRequest> findAll(
            @Param("productCategory") Integer productCategory,
            @Param("assetType") Integer assetType,
            @Param("modelNo") String modelNo,
            @Param("serialNo") String serialNo,
            @Param("approved") Integer approved,
            @Param("allocated") Integer allocated,
            @Param("requestFrom") Integer requestFrom,
            @Param("userId") BigInteger userId,
            @Param("pageable") Pageable pageable
    );

    @Query("select ar from AssetRequest ar where ar.id = (:id)")
    AssetRequest getAsset(@Param("id") BigInteger id);
}