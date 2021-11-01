package com.centram.common.dto;

import com.centram.domain.MediaFile;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 4540825991305702261L;
    private String oldPassword;
    private String newPassword;
    private MediaFile mediaFile;
}