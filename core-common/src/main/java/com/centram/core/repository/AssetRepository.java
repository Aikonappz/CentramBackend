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
    @Query(value = "select ao from Asset ao where a.organisation.id = (:organisationId) and " +
            " ( " +
            "   ((:status) = 'PENDING' and a.approvedUser1 = false and a.approvedUser2  = false ) " +
            "   OR " +
            "   ((:status) = 'PARTIALLY_APPROVED' and a.approvedUser1 = true and a.approvedUser2  = false ) " +
            "   OR " +
            "   ((:status) = 'APPROVED' and a.approvedUser1 = true and a.approvedUser2  = true ) " +
            "   OR " +
            "   ((:status) is null) " +
            " ) and " +
            " ( " +
            "   ((:assetNo) is not null and a.assetNo = (:assetNo)) " +
            "   OR " +
            "   ((:orderNo) is null) " +
            " ) "
    )
    Page<Asset> findAll(
            @Param("assetNo") String orderNo,
            @Param("status") String status,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );
}