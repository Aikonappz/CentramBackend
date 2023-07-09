package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.AssetType;
import com.centram.domain.enumarator.ProductCategory;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;


import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Action
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "asset_model")
public class AssetModel implements Serializable {
    private static final long serialVersionUID = -1033450307147452581L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    
    @NotNull
    @Valid
    @Column(name = "product_category")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private ProductCategory productCategory;

    
    @NotNull
    @Valid
    @Column(name = "asset_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private AssetType assetType;

    
    @NotNull
    @Column(name = "model_no", columnDefinition = "varchar(255) not null")
    private String modelNo;

    
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Status status;

    
    @NotNull
    @Column(name = "generate_asset_no")
    private Boolean generateAssetNo;
}