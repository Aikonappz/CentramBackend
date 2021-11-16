package com.centram.domain;

import com.centram.domain.converter.SubCategoryConverter;
import com.centram.domain.enumarator.CategoryType;
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

@ApiModel(description = "Categoty")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(
        name = "category",
        uniqueConstraints = @UniqueConstraint(name = "category_org_constraint", columnNames = {"name", "organisation_id"}),
        indexes = {
                @Index(name = "cat_org_idx", columnList = "organisation_id", unique = false),
        }
)
@Audited
public class Category extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575107124473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    private String name;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @Lob
    @Column(name = "subCategorys", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = SubCategoryConverter.class)
    private List<String> subCategorys;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "category_type")
    @Enumerated(EnumType.ORDINAL)
    private CategoryType categoryType;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    private Organisation organisation;

    public Category(@NotNull BigInteger id) {
        this.id = id;
    }

    public Category(@NotNull BigInteger id, @NotNull Long version) {
        super(version);
        this.id = id;
    }
}