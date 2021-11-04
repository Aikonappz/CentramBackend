package com.centram.common.dto;

import com.centram.domain.BaseEntity;
import com.centram.domain.MediaFile;
import com.centram.domain.Organisation;
import com.centram.domain.User;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrganisationDTO implements Serializable {
    private static final long serialVersionUID = -153641832136952253L;
    private Organisation organisation;
    private User user;
    private MediaFile mediaFile;
}