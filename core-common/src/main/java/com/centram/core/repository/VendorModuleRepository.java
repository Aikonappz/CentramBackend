package com.centram.core.repository;


import com.centram.domain.VendorModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface VendorModuleRepository extends JpaRepository<VendorModule, BigInteger> {
    /*@Modifying
    @Query("delete from DistributionListModule where id = (:id)")
    Integer deleteDistributionListModule(@Param("id") BigInteger id);*/
}