package com.centram.core.repository;

import com.centram.domain.RequisitionRecruiterTeamLead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface RequisitionRecruiterTeamLeadRepository extends JpaRepository<RequisitionRecruiterTeamLead, BigInteger> {

    Optional<RequisitionRecruiterTeamLead> findById(BigInteger id);
}
