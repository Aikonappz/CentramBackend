package com.centram.core.repository;

import com.centram.domain.NotificationTracker;
import com.centram.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface NotificationTrackerRepository extends JpaRepository<NotificationTracker, BigInteger> {

    boolean existsByOrganisationIdAndBusinessUnitIdAndDivisionIdAndDepartmentIdAndUserId(
            BigInteger organisationId,
            BigInteger businessUnitId,
            BigInteger divisionId,
            BigInteger departmentId,
            BigInteger userId
    );
}
