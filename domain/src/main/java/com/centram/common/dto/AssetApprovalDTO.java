package com.centram.common.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AssetApprovalDTO implements Serializable {
    private static final long serialVersionUID = -1177123685524269476L;
    private BigInteger id;
    private Integer approverNo;
    private Boolean approval;
    private String feedback;
}