package com.centram.common.vo;

import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PermissionVO implements Serializable {
    private static final long serialVersionUID = 2203272088452468360L;
    private BigInteger id;
    private RoleVO role;
    private ModuleVO module;
    private SubModuleVO submodule;
    private ActionVO action;

}