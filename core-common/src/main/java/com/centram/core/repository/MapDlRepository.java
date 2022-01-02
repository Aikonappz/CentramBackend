package com.centram.core.repository;


import com.centram.domain.MapDL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface MapDlRepository extends JpaRepository<MapDL, BigInteger> {
    @Query("select mdl from MapDL mdl where upper(mdl.dlName) = upper((:name)) and dlName.organisation.id = (:organisationId)")
    MapDL getByName(@Param("locationName") String name, @Param("organisationId") BigInteger organisationId);

    @Query("select mdl from MapDL mdl where mdl.organisation.id = (:organisationId)")
    Page getMapDLByOrganisation(@Param("organisationId") BigInteger organisationId, @Param("pageable") Pageable pageable);
}