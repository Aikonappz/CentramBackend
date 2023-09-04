package com.centram.core.repository;


import com.centram.common.vo.OrgAdminDashboardVO;
import com.centram.domain.Project;
import com.centram.domain.Vendor;
import com.centram.domain.enumarator.ProjectType;
import com.centram.domain.enumarator.VendorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, BigInteger> {
    @Query("select p from Project p join p.organisation org where UPPER(p.name) = UPPER((:name)) and org.id = (:organisationId)")
    Project getByName(@Param("name") String name, @Param("organisationId") BigInteger organisationId);

    @Query("select p from Project p join p.organisation org where p.projectType = (:projectType) and UPPER(p.name) = UPPER((:name)) and org.id = (:organisationId)")
    Project getByNameAndType(@Param("projectType") ProjectType projectType, @Param("name") String name, @Param("organisationId") BigInteger organisationId);

    @Query("select p from Project p join p.organisation org where org.id = (:organisationId) and " +
            "  ( " +
            "    ((:projectType) = 0 and 1 = 1) " +
            "    OR " +
            "    ((:projectType) <> 0 and p.projectType = (:projectType)) " +
            "  )" +
            "  and " +
            "  ( " +
            "    ((:hasFilter) = true and p.inHouse = (:inHouseFilter)) " +
            "    OR " +
            "    ((:hasFilter) = false) " +
            "  ) "
    )
    Page getByOrganisation(
            @Param("hasFilter") Boolean hasFilter,
            @Param("inHouseFilter") Boolean inHouseFilter,
            @Param("projectType") Integer projectType,
            @Param("organisationId") BigInteger organisationId,
            @Param("pageable") Pageable pageable
    );

}