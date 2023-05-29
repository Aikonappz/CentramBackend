package com.centram.common.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectDeallocateDTO implements Serializable {
    private static final long serialVersionUID = 4866236432397011772L;
    private BigInteger projectId;
    private List<BigInteger> userIds;
}