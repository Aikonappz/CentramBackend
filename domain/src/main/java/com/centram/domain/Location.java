package com.centram.domain;

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
import java.time.LocalTime;

/**
 * Location
 */
@ApiModel(description = "Location")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(
        name = "location",
        uniqueConstraints = @UniqueConstraint(name = "location_org_constraint", columnNames = {"name", "organisation_id"}),
        indexes = {
                @Index(name = "loc_org_idx", columnList = "organisation_id", unique = false),
        }
)
public class Location extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -8580165582808522922L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "country", columnDefinition = "varchar(255) default null")
    private String country;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "state", columnDefinition = "varchar(255) default null")
    private String state;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "city", columnDefinition = "varchar(255) default null")
    private String city;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "timezone", columnDefinition = "varchar(255) default null")
    private String timezone;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "name", columnDefinition = "varchar(255) not null")
    private String name;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "ops_start_time", nullable = false)
    private LocalTime opsStartTime;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "ops_end_time", nullable = false)
    private LocalTime opsEndTime;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    private Organisation organisation;
}