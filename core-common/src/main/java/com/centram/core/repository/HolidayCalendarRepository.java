package com.centram.core.repository;


import com.centram.domain.HolidayCalendar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface HolidayCalendarRepository extends JpaRepository<HolidayCalendar, BigInteger> {
    /**
     * @param organisationId
     * @param pageable
     * @return
     */
    @Query("select hc from HolidayCalendar hc where hc.organisation.id = (:organisationId)")
    Page<HolidayCalendar> getHolidayCalendars(@Param("organisationId") BigInteger organisationId, @Param("pageable") Pageable pageable);

    /**
     * @param accountId
     * @param year
     * @param locationId
     * @param organisationId
     * @return
     */
    @Query("select hc from HolidayCalendar hc where hc.location.id = (:locationId) and hc.year = (:year) and hc.organisation.id = (:organisationId) and hc.account.id = (:accountId)")
    HolidayCalendar getHolidays(@Param("accountId") BigInteger accountId, @Param("year") String year, @Param("locationId") BigInteger locationId, @Param("organisationId") BigInteger organisationId);
}