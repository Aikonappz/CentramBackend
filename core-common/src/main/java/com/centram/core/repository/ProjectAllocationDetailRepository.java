package com.centram.core.repository;


import com.centram.domain.Project;
import com.centram.domain.ProjectAllocationDetail;
import com.centram.domain.enumarator.PriorityType;
import com.centram.domain.enumarator.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ProjectAllocationDetailRepository extends JpaRepository<ProjectAllocationDetail, BigInteger> {
    @Query("select pad from ProjectAllocationDetail pad where pad.project.id = (:projectId) and pad.user.id in (:userIds)")
    List<ProjectAllocationDetail> getDeallocationList(@Param("projectId") BigInteger projectId, @Param("userIds") List<BigInteger> userIds);
}