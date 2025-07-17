package com.centram.core.repository;

import com.centram.domain.RequisitionRecruiterReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RequisitionRecruiterReviewRepository extends JpaRepository<RequisitionRecruiterReview, BigInteger> {
}
