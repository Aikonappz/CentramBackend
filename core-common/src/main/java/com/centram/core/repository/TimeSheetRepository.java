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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSheetRepository extends JpaRepository<TimeSheet, BigInteger> {
    @Query(value = "select ts.* from time_sheet ts where ts.user_id = (:userId) " +
            " and (case when :projectId < 0 then 1=1 else (select count(1) from time_sheet_entry tse where tse.time_sheet_id=ts.id and tse.project_id=:projectId) > 0 end) " +
            " and case when :date is null then 1=1 else :date between ts.start_date and ts.end_date end", nativeQuery = true)
    Page<TimeSheet> getTimeSheetByUser(
            @Param("date") LocalDate date,
            @Param("projectId") BigInteger projectId,
            @Param("userId") BigInteger userId,
            @Param("pageable") Pageable pageable
    );

    @Query(value = "select ts.* from time_sheet ts where (select count(1) from time_sheet_entry tse where tse.time_sheet_id=ts.id and tse.approver_id = (:approverId)) > 0 " +
            " and (case when :pendingApproval < 0 then 1=1 else (select count(1) from time_sheet_entry tse where tse.approver_comment is null and tse.approved = 0 and tse.rejected = 0 and tse.time_sheet_id=ts.id) > 0 end) " +
            " and (case when :projectId < 0 then 1=1 else (select count(1) from time_sheet_entry tse where tse.time_sheet_id=ts.id and tse.project_id=:projectId) > 0 end) " +
            " and (case when :userId < 0 then 1=1 else ts.user_id=:userId end)" +
            " and (case when :date is null then 1=1 else :date between ts.start_date and ts.end_date end)", nativeQuery = true)
    Page<TimeSheet> getPendingApprovalTimeSheets(
            @Param("pendingApproval") Integer pendingApproval,
            @Param("projectId") BigInteger projectId,
            @Param("approverId") BigInteger approverId,
            @Param("date") LocalDate date,
            @Param("userId") BigInteger userId,
            @Param("pageable") Pageable pageable
    );

    @Query(value ="select ts from TimeSheet ts where ts.user.id = (:userId)")
    List<TimeSheet> getTimeSheetByUser(@Param("userId") BigInteger userId);

   /* @Query(value = "select ts from TimeSheet ts join ts.timeSheetEntries tse where tse.approver.id = (:approverId) " +

            "and CASE WHEN :pendingApproval is null THEN 1=1 ELSE tse.approverComment is null END "+
            *//*" and (case when (:pendingApproval) < 0 then 1 = 1 else tse.approverComment is null and tse.approved = 0 and tse.rejected = 0 end) " +
            " and (case when (:projectId) < 0 then 1 = 1 else tse.project.id = (:projectId) end) " +
            " and (case when (:userId) < 0 then 1 = 1 else ts.user.id = (:userId) end)" +
            " and (case when (:date) is null then 1 = 1 else (:date) between ts.startDate and ts.endDate end)" +*//*
            "")
    Page<TimeSheet> getPendingApprovalTimeSheets(
            @Param("pendingApproval") Integer pendingApproval,
            //@Param("projectId") BigInteger projectId,
            @Param("approverId") BigInteger approverId,
            //@Param("date") LocalDate date,
            //@Param("userId") BigInteger userId,
            @Param("pageable") Pageable pageable
    );*/
}