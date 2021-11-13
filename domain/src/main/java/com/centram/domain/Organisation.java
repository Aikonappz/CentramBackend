package com.centram.domain;

import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@ApiModel(description = "Organisation")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "organisation",
        indexes = {
                @Index(name = "org_name_idx", columnList = "name", unique = false)
        }
)
@Audited
public class Organisation extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "name", columnDefinition = "varchar(255) not null")
    private String name;

    @ApiModelProperty(value = "")
    @Column(name = "add1", columnDefinition = "varchar(255)")
    private String add1;

    @ApiModelProperty(value = "")
    @Column(name = "add2", columnDefinition = "varchar(255) not null")
    private String add2;

    @ApiModelProperty(value = "")
    @Column(name = "city", columnDefinition = "varchar(255)")
    private String city;

    @ApiModelProperty(value = "")
    @Column(name = "pincode", columnDefinition = "varchar(255)")
    private String pincode;

    @ApiModelProperty(value = "")
    @Column(name = "pan", columnDefinition = "varchar(255)")
    private String pan;

    @ApiModelProperty(value = "")
    @Column(name = "tan", columnDefinition = "varchar(255)")
    private String tan;

    @ApiModelProperty(value = "")
    @Column(name = "gstin", columnDefinition = "varchar(255)")
    private String gstin;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "license_Type")
    @Enumerated(EnumType.ORDINAL)
    private LicenseType licenseType;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "license_start", nullable = true)
    private LocalDateTime licenseStart;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "license_end", nullable = true)
    private LocalDateTime licenseEnd;

    public Organisation(@NotNull BigInteger id) {
        this.id = id;
    }

    public Organisation(@NotNull BigInteger id, @NotNull Long version) {
        super(version);
        this.id = id;
    }
}