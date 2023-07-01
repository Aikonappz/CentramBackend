package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.HolidayConverter;
import com.fasterxml.jackson.annotation.JsonView;


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

@Validated

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

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    
    @NotNull
    @Column(name = "year")
    private String year;

    
    @Valid
    @Lob
    @Column(name = "holidays", nullable = false, columnDefinition = "TEXT not null")
    @Convert(converter = HolidayConverter.class)
    private List<Holiday> holidays;

    
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    private Organisation organisation;

    
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "location_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Location location;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
}