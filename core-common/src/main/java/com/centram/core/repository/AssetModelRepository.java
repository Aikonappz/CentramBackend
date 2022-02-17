package com.centram.core.repository;


import com.centram.domain.AssetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AssetModelRepository extends JpaRepository<AssetModel, BigInteger> {
}