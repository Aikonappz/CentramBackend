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
public class PermissionDTO implements Serializable {
    private static final long serialVersionUID = -3734191239665140808L;
    private BigInteger roleId;
    private List<BigInteger> moduleIds;
    private List<BigInteger> actionIds;
}