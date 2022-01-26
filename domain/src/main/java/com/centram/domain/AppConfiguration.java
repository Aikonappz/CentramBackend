package com.centram.domain;

import com.centram.domain.converter.ConfigurationPropertiesConverter;
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
import java.util.Map;

@ApiModel(description = "AppConfig")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "app_configuration", indexes = {@Index(name = "config_key_idx", columnList = "configuration_key", unique = true)})
public class AppConfiguration implements Serializable {

    private static final long serialVersionUID = 4216858740061734277L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "configuration_key", unique = true, nullable = false, columnDefinition = "varchar(255) not null")
    private String configurationKey;

    @ApiModelProperty(value = "")
    @Valid
    @NotNull
    @Lob
    @Column(name = "configuration_value", nullable = false, columnDefinition = "TEXT not null")
    private String configurationValue;

    @ApiModelProperty(value = "")
    @Valid
    @NotNull
    @Lob
    @Column(name = "configuration_properties", nullable = false, columnDefinition = "TEXT not null")
    @Convert(converter = ConfigurationPropertiesConverter.class)
    private Map<String, Object> configurationProperties;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;
}