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
public class ProjectUATRequestDTO implements Serializable {
    private static final long serialVersionUID = -6360757448965142736L;
    private BigInteger projectId;
    private BigInteger moduleId;
    private BigInteger subModuleId;
}