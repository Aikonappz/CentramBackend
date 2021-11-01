package com.centram.common.vo;


import com.centram.domain.enumarator.Status;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ModuleVO implements Serializable {
    private static final long serialVersionUID = -3340472890388412064L;
    private BigInteger id;
    private String name;
    private List<SubModuleVO> submodules;
    private Status status;

}