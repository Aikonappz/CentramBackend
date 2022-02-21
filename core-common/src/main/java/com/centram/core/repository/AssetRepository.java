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
            " ) "
    )
    Page<Asset> findAll(
            @Param("serialNo") String serialNo,
            @Param("organisationId") BigInteger organisationId,
            Pageable pageable
    );
}