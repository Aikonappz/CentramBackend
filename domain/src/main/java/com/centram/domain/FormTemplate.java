package com.centram.domain;

import com.centram.domain.converter.WatchListConverter;
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
import java.util.List;

@ApiModel(description = "Form Template")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "form_template")
@Audited
public class FormTemplate extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1704980863958691973L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "form_type", nullable = false)
    private String formType;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Lob
    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "template", nullable = false)
    private String template;

    @Lob
    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "group_ignore_fields", nullable = false)
    @Convert(converter = WatchListConverter.class)
    private List<String> groupIgnoreFields;


    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    public FormTemplate(@NotNull BigInteger id) {
        this.id = id;
    }
}