package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.AssetType;
import com.centram.domain.enumarator.ProductCategory;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Asset
 */
@ApiModel(description = "Asset  Request")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "asset_request")
@Audited
public class AssetRequest extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575291234473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "product_category", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private ProductCategory productCategory;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "asset_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private AssetType assetType;

    @ApiModelProperty(required = false, value = "")
    @NotNull
    @Column(name = "model_no", nullable = true, columnDefinition = "varchar(255) default null")
    @JsonView(Views.BasicView.class)
    private String modelNo;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "long_term", nullable = false)
    @JsonView(Views.BasicView.class)
    private Boolean longTerm = false;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "comment", nullable = false, columnDefinition = "varchar(2000) not null")
    @JsonView(Views.BasicView.class)
    private String comment;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "it_team_comment", nullable = false, columnDefinition = "varchar(2000) not null")
    @JsonView(Views.BasicView.class)
    private String itTeamComment;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "approved", nullable = false)
    @JsonView(Views.BasicView.class)
    private Boolean approved = false;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "approver_comment", nullable = true, columnDefinition = "varchar(2000)")
    @JsonView(Views.BasicView.class)
    private String approverComment;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "allocated", nullable = false)
    @JsonView(Views.BasicView.class)
    private Boolean allocated = false;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "request_canceled", nullable = false)
    @JsonView(Views.BasicView.class)
    private Boolean requestCanceled = false;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "asset_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Asset asset;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User user;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    @Transient
    private MediaFile attachment;

    public AssetRequest(@NotNull BigInteger id) {
        this.id = id;
    }

    public AssetRequest(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}