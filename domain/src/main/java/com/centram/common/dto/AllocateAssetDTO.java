package com.centram.common.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AllocateAssetDTO implements Serializable {
    private static final long serialVersionUID = -1177123688724269476L;
    private BigInteger requestId;
    private BigInteger assetId;
    private Boolean allocate;
    private String feedback;
}