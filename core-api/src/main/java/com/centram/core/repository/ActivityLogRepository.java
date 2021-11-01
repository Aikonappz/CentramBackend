package com.centram.core.repository;


import com.centram.domain.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, BigInteger> {
    @Query("select a from ActivityLog a where a.userId = (:userId)")
    Page<ActivityLog> getActivities(@Param("userId") BigInteger userId, Pageable pageable);
}