package com.centram.domain;

import com.centram.domain.enumarator.ActivityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "activity_log",
        indexes = {
                @Index(name = "user_id_idx", columnList = "user_id", unique = false),
                @Index(name = "organisation_id_idx", columnList = "organisation_id", unique = false),
                @Index(name = "activity_type_idx", columnList = "activity_type", unique = false),
                @Index(name = "user_id_activity_type_organisation_id_idx", columnList = "user_id,activity_type,organisation_id", unique = false),
                @Index(name = "user_id_activity_type_idx", columnList = "user_id,activity_type", unique = false),
                @Index(name = "user_id_organisation_id_idx", columnList = "user_id,organisation_id", unique = false),
                @Index(name = "activity_type_organisation_id_idx", columnList = "activity_type,organisation_id", unique = false),
        }
)
public class ActivityLog implements Serializable {
    private static final long serialVersionUID = -6554446568157662441L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "user_id")
    private BigInteger userId;

    @Column(name = "organisation_id")
    private BigInteger organisationId;

    @Column(name = "activity_type")
    private ActivityType activityType;

    @Column(name = "activity_date_time")
    private LocalDateTime activityDateTime;

    public ActivityLog(BigInteger userId, BigInteger organisationId, ActivityType activityType) {
        this.userId = userId;
        this.organisationId = organisationId;
        this.activityType = activityType;
        this.activityDateTime = LocalDateTime.now();
    }
}