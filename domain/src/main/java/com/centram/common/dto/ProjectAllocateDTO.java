package com.centram.common.dto;

import com.centram.domain.enumarator.ProjectType;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectAllocateDTO implements Serializable {
    private static final long serialVersionUID = 4866236432397011772L;
    private ProjectType projectType;
    private String maxAllocation;
    private List<BigInteger> projectIds;
    private List<BigInteger> userIds;
}