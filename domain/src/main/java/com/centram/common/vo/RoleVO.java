package com.centram.common.vo;


import com.centram.domain.enumarator.Status;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoleVO implements Serializable {
    private static final long serialVersionUID = -8883781133065226600L;
    private BigInteger id;
    private String name;
    private String userGroup;
    private Integer level;
    private Status status;
}