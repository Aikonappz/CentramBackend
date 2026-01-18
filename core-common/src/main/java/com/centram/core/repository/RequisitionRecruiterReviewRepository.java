package com.centram.core.repository;

import com.centram.domain.RequisitionRecruiterReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface RequisitionRecruiterReviewRepository extends JpaRepository<RequisitionRecruiterReview, BigInteger> {
    Optional<RequisitionRecruiterReview> findByRequisitionId(BigInteger requisitionId);
}
