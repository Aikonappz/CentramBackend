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
public class SubModuleVO implements Serializable {
    private static final long serialVersionUID = 7806425213468692993L;
    private BigInteger id;
    private String name;
    //private ModuleVO module;
    private Status status;
}