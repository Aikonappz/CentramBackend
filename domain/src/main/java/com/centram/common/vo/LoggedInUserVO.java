package com.centram.common.vo;


import com.centram.common.dto.LoggedInUserDTO;
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
    private String userName;
    private MediaFile profileImage;
    private HashMap<String, String> modulePermissions;

    public LoggedInUserVO(MediaFile profileImage, LoggedInUserDTO loggedInUserDTO) {
        this.userId = loggedInUserDTO.getUserId();
        this.organisationId = loggedInUserDTO.getOrganisationId();
        this.appManager = loggedInUserDTO.getAppManager();
        this.userName = loggedInUserDTO.getUserName();
        this.profileImage = profileImage;
        this.modulePermissions = loggedInUserDTO.getModulePermissions();
    }
}
