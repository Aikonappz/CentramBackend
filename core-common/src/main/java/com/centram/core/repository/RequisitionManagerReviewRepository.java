package com.centram.core.repository;

import com.centram.domain.RequisitionManagerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface RequisitionManagerReviewRepository extends JpaRepository<RequisitionManagerReview, BigInteger> {
    Optional<RequisitionManagerReview> findByRequisitionId(BigInteger requisitionId);
}
