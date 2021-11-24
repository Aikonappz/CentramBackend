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
        uniqueConstraints = {
                @UniqueConstraint(name = "user_email_org_constraint", columnNames = {"email", "organisation_id"}),
                @UniqueConstraint(name = "user_empid_org_constraint", columnNames = {"employee_id", "organisation_id"})
        },
        indexes = {
                @Index(name = "email_indx", columnList = "email", unique = true),
                @Index(name = "employeeId_indx", columnList = "employee_id", unique = false),
                @Index(name = "managerId_indx", columnList = "manager_id", unique = false),
                @Index(name = "employeeId_org_indx", columnList = "employee_id,organisation_id", unique = false),
                @Index(name = "managerId__org_indx", columnList = "manager_id,organisation_id", unique = false),
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

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "first_name", nullable = false, columnDefinition = "varchar(255) not null")
    private String firstName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "last_name", nullable = false, columnDefinition = "varchar(255) not null")
    private String lastName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "email", nullable = false, columnDefinition = "varchar(255) not null")
    private String email;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "password", nullable = false, columnDefinition = "varchar(255) not null")
    private String password;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "contact_no", nullable = false, columnDefinition = "varchar(255) not null")
    private String contactNo;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "sec_contact_no", nullable = true, columnDefinition = "varchar(255) not null")
    private String secContactNo;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "employee_id", nullable = true, columnDefinition = "varchar(255)")
    private String employeeId;

    @ApiModelProperty(required = false, value = "")
    //@OneToOne(optional = true, fetch = FetchType.LAZY)
    //@JoinColumn(name = "manager_id", referencedColumnName = "id")
    @Column(name = "manager_id", nullable = true, columnDefinition = "BIGINT default null")
    private BigInteger managerId;

    @ApiModelProperty(value = "")
    @Column(name = "project_code", nullable = true, columnDefinition = "varchar(255) default null")
    private String projectCode;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @Lob
    @Column(name = "roles", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = RoleConverter.class)
    private List<BigInteger> roles;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    private Organisation organisation;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "location_id", nullable = true, referencedColumnName = "id")
    private Location location;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "department_id", nullable = true, referencedColumnName = "id")
    private Department department;

    public User(@NotNull BigInteger id) {
        this.id = id;
    }

    public User(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}