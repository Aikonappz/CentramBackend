package com.centram.core.repository;


import com.centram.common.vo.OrgAdminDashboardVO;
import com.centram.domain.TimeSheet;
import com.centram.domain.Vendor;
import com.centram.domain.enumarator.VendorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSheetRepository extends JpaRepository<TimeSheet, String> {

    TimeSheet findByReferenceId(@Param("referenceId") UUID referenceId);

    @Query("select ts from TimeSheet ts join ts.user usr where usr.id = (:userId) ")
    Page<TimeSheet> getTimeSheetByUser(@Param("userId") BigInteger userId, @Param("pageable") Pageable pageable);

}