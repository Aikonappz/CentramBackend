package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.ModelConverter;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * App Modules
 */
@ApiModel(description = "Module")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "module",
        uniqueConstraints = {
                @UniqueConstraint(name = "mod_submod_constraint", columnNames = {"name", "parent_module_id"})
        }
)
public class Module implements Serializable {

    private static final long serialVersionUID = -4024535323361400625L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    private String name;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "parent_module_id", nullable = true, columnDefinition = "BIGINT default null")
    private BigInteger parentModuleId;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "app_module")
    private Boolean appModule;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "licence_type")
    @Enumerated(EnumType.ORDINAL)
    private LicenseType licenseType;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "customer_module_name")
    private String customerModuleName;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "asset_ops_name")
    private String assetOPSName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "ticketable")
    private Boolean ticketable;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "generate_asset_no")
    private Boolean generateAssetNo;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "require_approval")
    private Boolean requireApproval;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @Lob
    @Column(name = "models", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = ModelConverter.class)
    @JsonView(Views.BasicView.class)
    private List<BigInteger> models;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "dashboard_box_colour")
    private String dashboardBoxColour;

    @Transient
    private String parentModuleName;

    public Module(BigInteger id) {
        this.id = id;
    }
}