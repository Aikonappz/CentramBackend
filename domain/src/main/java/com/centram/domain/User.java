package com.centram.domain;

import com.centram.domain.converter.RoleConverter;
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

/**
 * User
 */
@ApiModel(description = "User")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "user",
        indexes = {
                @Index(name = "email_indx", columnList = "email", unique = true),
                @Index(name = "org_id_idx", columnList = "organisation_id", unique = false),
        }
)
@Audited
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "first_name", columnDefinition = "varchar(255) not null")
    private String firstName;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "last_name", columnDefinition = "varchar(255) not null")
    private String lastName;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "email", columnDefinition = "varchar(255) not null")
    private String email;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "password", columnDefinition = "varchar(255) not null")
    private String password;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "contact_no", columnDefinition = "varchar(255) not null")
    private String contactNo;

    @ApiModelProperty(value = "")
    @Column(name = "sec_contact_no", columnDefinition = "varchar(255) not null")
    private String secContactNo;

    @ApiModelProperty(value = "")
    @Column(name = "employee_id", columnDefinition = "varchar(255)")
    private String employeeId;

    @ApiModelProperty(value = "")
    @Column(name = "manager_id", columnDefinition = "varchar(255)")
    private BigInteger managerId;

    @ApiModelProperty(value = "")
    @Column(name = "project_code", columnDefinition = "varchar(255)")
    private String projectCode;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @Lob
    @Column(name = "roles", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = RoleConverter.class)
    private List<BigInteger> roles;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    private Organisation organisation;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    public User(@NotNull BigInteger id) {
        this.id = id;
    }

    public User(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}