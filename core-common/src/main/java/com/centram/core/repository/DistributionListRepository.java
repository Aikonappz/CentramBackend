package com.centram.core.repository;


import com.centram.domain.DistributionList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DistributionListRepository extends JpaRepository<DistributionList, BigInteger> {
    @Query("select mdl from DistributionList mdl join mdl.organisation org where upper(mdl.dlName) = upper((:name)) and org.id = (:organisationId)")
    DistributionList getByName(@Param("name") String name, @Param("organisationId") BigInteger organisationId);

    @Query("select mdl from DistributionList mdl join mdl.distributionListModules dlm join mdl.organisation org where dlm.moduleId = (:moduleId) and dlm.subModuleId = (:subModuleId) and org.id = (:organisationId)")
    List<DistributionList> getByModuleIdAndSubModuleId(@Param("moduleId") BigInteger moduleId, @Param("subModuleId") BigInteger subModuleId, @Param("organisationId") BigInteger organisationId);

    @Query("select mdl from DistributionList mdl join mdl.organisation org where org.id = (:organisationId)")
    Page getMapDLByOrganisation(@Param("organisationId") BigInteger organisationId, @Param("pageable") Pageable pageable);
}