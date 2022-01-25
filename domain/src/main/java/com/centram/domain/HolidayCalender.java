package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.HolidayConverter;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * HolidayCalender
 */
@ApiModel(description = "HolidayCalender")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Audited
@Entity
@Table(name = "holiday_calender", indexes = {
        @Index(name = "year_loc_org_idx", columnList = "year,location_id,organisation_id", unique = true)
})
public class HolidayCalender extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -1039150309367452581L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "year")
    private String year;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @Lob
    @Column(name = "holidays", nullable = false, columnDefinition = "TEXT not null")
    @Convert(converter = HolidayConverter.class)
    private List<Holiday> holidays;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    private Organisation organisation;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "location_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Location location;
}