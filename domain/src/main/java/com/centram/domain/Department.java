package com.centram.domain;

import com.centram.domain.enumarator.Status;
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
 * Department
 */
@ApiModel(description = "Department")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Audited
@Table(name = "department", indexes = {@Index(name = "department_name_idx", columnList = "name", unique = true)})
public class Department extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7163306376698505219L;

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

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;
}