package com.centram.domain;

import com.centram.domain.enumarator.IncidentNotificationType;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IncidentNotification implements Serializable {
    private static final long serialVersionUID = -1177813612524269476L;
    private BigInteger incidentId;
    private IncidentNotificationType incidentNotificationType;
    private LocalDateTime time;
}