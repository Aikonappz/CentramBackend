package com.centram.common.vo;


import com.centram.domain.enumarator.NotificationType;
import com.centram.domain.enumarator.Status;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationVO implements Serializable {
    private static final long serialVersionUID = -6523446568157662441L;
    private BigInteger id;
    private String title;
    private String body;
    private Status status;
    private NotificationType notificationType;
    private BigInteger userId;
}