package com.centram.domain;

import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Action
 */
@ApiModel(description = "MediaFile")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "media_file",
        indexes = {
                @Index(name = "media_srch_idx", columnList = "entity_type,media_type,entity_id", unique = false)
        }
)
public class MediaFile implements Serializable {
    private static final long serialVersionUID = 5381555771178250038L;

    @ApiModelProperty(required = true, value = "")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "file_type", nullable = false)
    private String fileType;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "entity_type")
    @Enumerated(EnumType.ORDINAL)
    private EntityType entityType;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "media_type")
    @Enumerated(EnumType.ORDINAL)
    private MediaType mediaType;

    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "entity_id", columnDefinition = "BIGINT")
    private BigInteger entityId;
}