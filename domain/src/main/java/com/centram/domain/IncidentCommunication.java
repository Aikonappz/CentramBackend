package com.centram.domain;

import com.centram.common.view.Views;
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
import java.util.List;

/**
 * Incident Communication
 */
@ApiModel(description = "Incident Communication")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Audited
@Entity
@Table(name = "incident_communication", indexes = {
        @Index(name = "incident_idx", columnList = "incident_id", unique = false)
})
public class IncidentCommunication extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -1033450309367452581L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Lob
    @Column(name = "message", columnDefinition = "TEXT not null")
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private String message;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    @JsonView({Views.InternalView.class,})
    private Incident incident;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "communicated_by", nullable = true, referencedColumnName = "id")
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private User communicatedBy;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    @Transient
    private List<MediaFile> attachments;

    public IncidentCommunication(String message, Incident incident, User communicatedBy, List<MediaFile> attachments) {
        this.message = message;
        this.incident = incident;
        this.communicatedBy = communicatedBy;
        this.attachments = attachments;
    }
}