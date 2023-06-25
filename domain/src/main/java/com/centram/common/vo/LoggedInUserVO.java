package com.centram.common.vo;


import com.centram.common.dto.LoggedInUser;
import com.centram.domain.MediaFile;
import com.centram.domain.enumarator.LicenseType;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LoggedInUserVO implements Serializable {

    private static final long serialVersionUID = -3205717675460229264L;
    private BigInteger userId;
    private String jwtToken;
    private BigInteger organisationId;
    private Boolean appManager;
    private String name;
    private String orgName;
    private String email;
    private String timeZone;
    private String location;
    private String department;
    private LicenseType licenseType;
    private MediaFile profileImage;
    private MediaFile organisationLogo;
    private List<String> roles;
    private List<PermissionVO> modulePermissions;
    private BigInteger accountId;
    private String accountName;
    private String accountNo;

    public LoggedInUserVO(LoggedInUser loggedInUser) {
        this.userId = loggedInUser.getUserId();
        this.organisationId = loggedInUser.getOrganisationId();
        this.appManager = loggedInUser.getAppManager();
        this.name = loggedInUser.getName();
        this.orgName = loggedInUser.getOrgName();
        this.email = loggedInUser.getEmail();
        this.profileImage = profileImage;
        this.modulePermissions = loggedInUser.getModulePermissions();
        this.jwtToken = loggedInUser.getAuthToken();
        this.department = loggedInUser.getDepartment();
        this.location = loggedInUser.getLocation();
        this.timeZone = loggedInUser.getTimeZone();
        this.roles = loggedInUser.getAuthorities().stream()
                .map(i -> i.getAuthority())
                .collect(Collectors.toList());
        this.licenseType = loggedInUser.getLicenseType();
        this.accountId = loggedInUser.getAccountId();
        this.accountName = loggedInUser.getAccountName();
        this.accountNo = loggedInUser.getAccountNo();
    }

    public LoggedInUserVO(MediaFile profileImage, LoggedInUser loggedInUser) {
        this.userId = loggedInUser.getUserId();
        this.organisationId = loggedInUser.getOrganisationId();
        this.appManager = loggedInUser.getAppManager();
        this.name = loggedInUser.getName();
        this.orgName = loggedInUser.getOrgName();
        this.email = loggedInUser.getEmail();
        this.profileImage = profileImage;
        this.modulePermissions = loggedInUser.getModulePermissions();
        this.jwtToken = loggedInUser.getAuthToken();
        this.department = loggedInUser.getDepartment();
        this.location = loggedInUser.getLocation();
        this.timeZone = loggedInUser.getTimeZone();
        this.roles = loggedInUser.getAuthorities().stream()
                .map(i -> i.getAuthority())
                .collect(Collectors.toList());
        this.licenseType = loggedInUser.getLicenseType();
        this.accountId = loggedInUser.getAccountId();
        this.accountName = loggedInUser.getAccountName();
        this.accountNo = loggedInUser.getAccountNo();
    }
}
