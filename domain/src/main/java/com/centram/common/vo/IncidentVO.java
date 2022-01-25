package com.centram.common.vo;


import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IncidentVO implements Serializable {
    private static final long serialVersionUID = -6554116568157662441L;
    private String incidentNo;
    private String requestedUserId;
    private String requestedUserName;
    private String category;
    private String subcate;

    private String name;
}