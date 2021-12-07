package com.centram.common.vo;

import com.centram.domain.Permission;
import com.centram.domain.enumarator.LicenseType;
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
    private BigInteger roleId;
    private String roleName;
    private BigInteger actionId;
    private String actionName;
    private BigInteger moduleId;
    private String moduleName;
    private BigInteger moduleParentId;
    private LicenseType licenseType;
    private Boolean appModule;
    private String customerModuleName;

    public PermissionVO(Permission permission) {
        this.moduleId = permission.getModule().getId();
        this.moduleParentId = permission.getModule().getParentModuleId();
        this.moduleName = permission.getModule().getName();
        this.appModule = permission.getModule().getAppModule();
        this.licenseType = permission.getModule().getLicenseType();
        this.customerModuleName = permission.getModule().getCustomerModuleName();
        this.actionId = permission.getAction().getId();
        this.actionName = permission.getAction().getName();
        this.roleId = permission.getRole().getId();
        this.roleName = permission.getRole().getName();
    }
}