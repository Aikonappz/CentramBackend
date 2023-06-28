package com.centram.core.repository;


import com.centram.domain.HolidayCalender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface HolidayCalenderRepository extends JpaRepository<HolidayCalender, BigInteger> {
    @Query("select hc from HolidayCalender hc where hc.organisation.id = (:organisationId)")
    Page getHolidayCalenderByOrganisation(@Param("organisationId") BigInteger organisationId, @Param("pageable") Pageable pageable);

    @Query("select hc from HolidayCalender hc where hc.location.id = (:locationId) and hc.year = (:year) and hc.organisation.id = (:organisationId) and hc.account.id = (:accountId)")
    HolidayCalender getHolidayCalenderByYear(
            @Param("accountId") BigInteger accountId,
            @Param("year") String year,
            @Param("locationId") BigInteger locationId,
            @Param("organisationId") BigInteger organisationId
    );
}