package com.centram.domain;

import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * App SubModules
 */
@ApiModel(description = "SubModule")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "sub_module",
        indexes = {
                @Index(name = "module_id_idx", columnList = "module_id", unique = false),
        }
)
public class SubModule implements Serializable {
    private static final long serialVersionUID = -3750795330490917985L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "name", columnDefinition = "varchar(255) not null")
    private String name;

    @ApiModelProperty(required = true, value = "")
    @JsonBackReference
    //@JsonIgnore
    //@JsonProperty("module")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private Module module;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;
}