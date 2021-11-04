package com.centram.common.vo;


import com.centram.common.dto.LoggedInUser;
import com.centram.domain.MediaFile;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LoggedInUserVO implements Serializable {

    private static final long serialVersionUID = -3205717675460229264L;
    private BigInteger userId;
    private BigInteger organisationId;
    private Boolean appManager;
    private String email;
    private MediaFile profileImage;
    private HashMap<String, String> modulePermissions;

    public LoggedInUserVO(MediaFile profileImage, LoggedInUser loggedInUser) {
        this.userId = loggedInUser.getUserId();
        this.organisationId = loggedInUser.getOrganisationId();
        this.appManager = loggedInUser.getAppManager();
        this.email = loggedInUser.getEmail();
        this.profileImage = profileImage;
        this.modulePermissions = loggedInUser.getModulePermissions();
    }
}
