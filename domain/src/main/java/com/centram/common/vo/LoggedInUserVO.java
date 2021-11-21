package com.centram.common.vo;


import com.centram.common.dto.LoggedInUser;
import com.centram.domain.MediaFile;
import com.centram.domain.Permission;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

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
    private MediaFile profileImage;
    private MediaFile organisationLogo;
    private List<PermissionVO> modulePermissions;

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
    }
}
