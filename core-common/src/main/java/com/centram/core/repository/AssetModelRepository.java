package com.centram.core.repository;


import com.centram.domain.AssetModel;
import com.centram.domain.enumarator.AssetType;
import com.centram.domain.enumarator.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AssetModelRepository extends JpaRepository<AssetModel, BigInteger> {
    AssetModel findByProductCategoryAndAssetTypeAndModelNo(
            @Param("productCategory") ProductCategory productCategory,
            @Param("assetType") AssetType assetType,
            @Param("modelNo") String modelNo
    );
}