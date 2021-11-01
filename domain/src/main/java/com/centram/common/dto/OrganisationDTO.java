package com.centram.common.dto;

import com.centram.domain.BaseEntity;
import com.centram.domain.MediaFile;
import com.centram.domain.Setting;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrganisationDTO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -153641832136952253L;
    private Setting setting;
    private MediaFile mediaFile;
}