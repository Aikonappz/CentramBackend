package com.centram.domain;

import com.centram.domain.converter.ConfigurationPropertiesConverter;
import com.centram.domain.enumarator.Status;


import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;


@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "app_configuration", indexes = {@Index(name = "config_key_idx", columnList = "configuration_key", unique = true)})
public class AppConfiguration implements Serializable {

    private static final long serialVersionUID = 4216858740061734277L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    
    @NotNull
    @Column(name = "configuration_key", unique = true, nullable = false, columnDefinition = "varchar(255) not null")
    private String configurationKey;

    
    @Valid
    @NotNull
    @Lob
    @Column(name = "configuration_value", nullable = false, columnDefinition = "longtext not null")
    private String configurationValue;

    
    @Valid
    @NotNull
    @Lob
    @Column(name = "configuration_properties", nullable = false, columnDefinition = "longtext not null")
    @Convert(converter = ConfigurationPropertiesConverter.class)
    private Map<String, Object> configurationProperties;

    
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;
}