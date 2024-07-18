package com.centram.core.repository;


import com.centram.common.vo.AllocationDetailVO;
import com.centram.domain.Project;
import com.centram.domain.ProjectAllocationDetail;
import com.centram.domain.User;
import com.centram.domain.enumarator.BillingType;
import com.centram.domain.enumarator.PriorityType;
import com.centram.domain.enumarator.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectAllocationDetailRepository extends JpaRepository<ProjectAllocationDetail, BigInteger> {
    @Query("select pad from ProjectAllocationDetail pad where pad.project.id = (:projectId) and pad.user.id in (:userIds)")
    List<ProjectAllocationDetail> getDeallocationList(@Param("projectId") BigInteger projectId, @Param("userIds") List<BigInteger> userIds);

    @Query("select pad from ProjectAllocationDetail pad where pad.user.id = (:userId)")
    List<ProjectAllocationDetail> getUserProjects(@Param("userId") BigInteger userId);

    @Query("select pad from ProjectAllocationDetail pad where pad.user.id in (:userIds)")
    List<ProjectAllocationDetail> getUsersProjects(@Param("userIds") List<BigInteger> userIds);

    @Query("select pad from ProjectAllocationDetail pad where pad.user.id = (:userId) and (:start) between pad.startDate and pad.endDate or (:end) between pad.startDate and pad.endDate")
    List<ProjectAllocationDetail> getUserProjects(@Param("userId") BigInteger userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "select p.technology as technology, p.project_type as projectType,  p.project_billing_type as projectBillingType, " +
            " m.name as moduleName, sm.name as subModuleName, p.name as name, p.code as code, pad.start_date as allocationStart, pad.end_date as allocationEnd, "  +
            " p.uat_start as start, p.uat_end as end, pad.max_allocation as maxAllocation, pad.created_date as allocatedAt," +
            " pad.modified_date as deallocatedAt, pad.deallocated as deallocated, concat(u.first_name, ' ',u.last_name) as userName, u.email as userEmail " +
            " from project_allocation_detail pad left join project p on (p.id = pad.project_id) left join module m on (m.id = p.module_id)  " +
            " left join module sm on (sm.id = p.sub_module_id) left join user u on (u.id = pad.user_id) " +
            " where p.organisation_id = :organisationId " +
            " and case when :deallocated < 0 then pad.deallocated = 0 else pad.deallocated = 1 end" +
            " and case when :billingType < 0 then 1 = 1 else p.project_billing_type = :billingType end " +
            " and case when :projectFilter < 0 then 1 = 1 else p.id in (:projects) end " +
            " and case when :start is null then 1 = 1 else pad.start_date >= :start end " +
            " and case when :end is null then 1 = 1 else pad.end_date <= :end end "
            , nativeQuery = true,
            countQuery = "select count(pad.id) from project_allocation_detail pad left join project p on (p.id = pad.project_id) left join module m on (m.id = p.module_id)  " +
                    " left join module sm on (sm.id = p.sub_module_id) left join user u on (u.id = pad.user_id) " +
                    " where p.organisation_id = :organisationId " +
                    " and case when :deallocated < 0 then pad.deallocated = 0 else pad.deallocated = 1 end" +
                    " and case when :billingType < 0 then 1 = 1 else p.project_billing_type = :billingType end " +
                    " and case when :projectFilter < 0 then 1 = 1 else p.id in (:projects) end " +
                    " and case when :start is null then 1 = 1 else pad.start_date >= :start end " +
                    " and case when :end is null then 1 = 1 else pad.end_date <= :end end "
    )
    Page<AllocationDetailVO> getAllocationDetail(
            @Param("organisationId") BigInteger organisationId,
            @Param("deallocated") Integer deallocated,
            @Param("billingType") Integer billingType,
            @Param("projectFilter") Integer projectFilter,
            @Param("projects") List<BigInteger> projects,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    @Query("select pad.user from ProjectAllocationDetail pad where pad.project.id = (:projectId) and pad.project.organisation.id = (:organisationId) " +
            " and " +
            "  ( " +
            "    (:includeDeallocated = true and 1=1) " +
            "    OR " +
            "    (:includeDeallocated = false and pad.deallocated = 0) " +
            "  ) "
    )
    Page<User> getUserProjects(@Param("organisationId") BigInteger organisationId, @Param("projectId") BigInteger projectId, @Param("includeDeallocated") Boolean includeDeallocated , Pageable pageable);
}