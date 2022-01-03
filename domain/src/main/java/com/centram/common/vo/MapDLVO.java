package com.centram.common.vo;


import com.centram.domain.DistributionList;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MapDLVO implements Serializable {
    private static final long serialVersionUID = -6554446568157662441L;
    private BigInteger id;
    private BigInteger moduleId;
    private String moduleName;
    private BigInteger subModuleId;
    private String subModuleName;
    private String dlName;
    private BigInteger organisationId;




}