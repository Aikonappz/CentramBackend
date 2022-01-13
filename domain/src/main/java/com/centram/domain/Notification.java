package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.NotificationType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * User
 */
@ApiModel(description = "Notification")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "notification",
        indexes = {
                @Index(name = "usr_notf_indx", columnList = "user_id", unique = false)
        }
)
@Audited
public class Notification extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2713337834473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "notification_title", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String notificationTitle;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Lob
    @Column(name = "notification_body", nullable = false, columnDefinition = "TEXT")
    @JsonView(Views.BasicView.class)
    private String notificationBody;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private User user;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Status status;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "notification_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private NotificationType notificationType;

    public Notification(@NotNull BigInteger id) {
        this.id = id;
    }

    public Notification(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }

    public Notification(String notificationTitle, String notificationBody, User user, Status status, NotificationType notificationType) {
        this.notificationTitle = notificationTitle;
        this.notificationBody = notificationBody;
        this.user = user;
        this.status = status;
        this.notificationType = notificationType;
    }
}