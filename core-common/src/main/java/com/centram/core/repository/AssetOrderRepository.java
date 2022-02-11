package com.centram.core.repository;


import com.centram.domain.AssetOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AssetOrderRepository extends JpaRepository<AssetOrder, BigInteger> {
    @Query(value = "select ao from AssetOrder ao where ao.organisation.id = (:organisationId) and " +
            " ( " +
            "   ((:status) = 'PENDING' and ao.approvedUser1 = false and ao.approvedUser2  = false ) " +
            "   OR " +
            "   ((:status) = 'PARTIALLY_APPROVED' and ao.approvedUser1 = true and ao.approvedUser2  = false ) " +
            "   OR " +
            "   ((:status) = 'APPROVED' and ao.approvedUser1 = true and ao.approvedUser2  = true ) " +
            "   OR " +
            "   ((:status) is null) " +
            " ) and " +
            " ( " +
            "   ((:orderNo) is not null and ao.orderNo = (:orderNo)) " +
            "   OR " +
            "   ((:orderNo) is null) " +
            " ) "
    )
    Page<AssetOrder> findAll(
            @Param("orderNo") String orderNo,
            @Param("status") String status,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );
}