package com.centram.common.vo;

import com.centram.domain.Location;
import com.centram.domain.enumarator.Status;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalTime;

/**
 * Location VO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LocationVO implements Serializable {
    private static final long serialVersionUID = -413751352385705740L;

    private BigInteger id;
    private String country;
    private String state;
    private String city;
    private String timezone;
    private String name;
    private String officeName;
    private LocalTime opsStartTime;
    private LocalTime opsEndTime;
    private String status;

    public LocationVO(Location location) {
        this.id = location.getId();
        this.country = location.getCountry();
        this.state = location.getState();
        this.city = location.getCity();
        this.timezone = location.getTimezone();
        this.name = location.getName();
        this.officeName = location.getOfficeName();
        this.opsStartTime = location.getOpsStartTime();
        this.opsEndTime = location.getOpsEndTime();
        this.status = location.getStatus().toString();
    }
}