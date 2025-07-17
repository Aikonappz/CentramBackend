package com.centram.core.repository;

import com.centram.domain.RequisitionManagerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface RequisitionManagerReviewRepository extends JpaRepository<RequisitionManagerReview, BigInteger> {
}
