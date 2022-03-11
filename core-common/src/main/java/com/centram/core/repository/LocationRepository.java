package com.centram.core.repository;


import com.centram.domain.Location;
import com.centram.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface LocationRepository extends PagingAndSortingRepository<Location, BigInteger> {
    @Query("select l from Location l where UPPER(l.name) = UPPER((:locationName)) and l.organisation.id = (:organisationId)")
    Location getByLocationName(@Param("locationName") String locationName, @Param("organisationId") BigInteger organisationId);

    @Query("select l from Location l where UPPER(l.officeName) = UPPER((:officeName)) and l.organisation.id = (:organisationId)")
    Location getByOfficeName(@Param("officeName") String officeName, @Param("organisationId") BigInteger organisationId);

    @Query("select l from Location l where l.organisation.id = (:organisationId)")
    Page getLocationByOrganisation(@Param("organisationId") BigInteger organisationId, @Param("pageable") Pageable pageable);

    @Modifying
    @Query("update Location set status = (:status) where id in (:locationIds)")
    Integer updateStatus(@Param("status") Status status, @Param("locationIds") List<BigInteger> locationIds);
}
