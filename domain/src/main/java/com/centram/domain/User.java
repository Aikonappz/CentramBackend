package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.common.vo.UserVO;
import com.centram.domain.converter.RoleConverter;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;


import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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

@JsonInclude(JsonInclude.Include.NON_NULL)
@Validated

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

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    
    @NotNull
    @Column(name = "first_name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String firstName;

    
    @NotNull
    @Column(name = "last_name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String lastName;

    
    @NotNull
    @Column(name = "email", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String email;

    
    @NotNull
    @Column(name = "password", nullable = false, columnDefinition = "varchar(255) not null")
    private String password;

    
    @NotNull
    @Column(name = "contact_no", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String contactNo;

    
    @Column(name = "sec_contact_no", nullable = true, columnDefinition = "varchar(255)")
    @JsonView(Views.BasicView.class)
    private String secContactNo;

    
    @Column(name = "employee_id", nullable = true, columnDefinition = "varchar(255)")
    @JsonView(Views.BasicView.class)
    private String employeeId;

    
    //@OneToOne(optional = true, fetch = FetchType.LAZY)
    //@JoinColumn(name = "manager_id", referencedColumnName = "id")
    @Column(name = "manager_id", nullable = true, columnDefinition = "BIGINT default null")
    @JsonView(Views.BasicView.class)
    private BigInteger managerId;

    
    @Column(name = "project_code", nullable = true, columnDefinition = "varchar(255) default null")
    @JsonView(Views.BasicView.class)
    private String projectCode;

    
    @Valid
    @NotNull
    @Lob
    @Column(name = "roles", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = RoleConverter.class)
    @JsonView(Views.BasicView.class)
    private List<BigInteger> roles;

    
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Status status;

    
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    private Organisation organisation;

    
    @Valid
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = true, referencedColumnName = "id")
    private Vendor vendor;

    
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "location_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Location location;

    
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "department_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Department department;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    public User(@NotNull BigInteger id) {
        this.id = id;
    }

    public User(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }

    public User(UserVO userVO) {
        super(userVO.getVersion());
        this.id = userVO.getId();
        this.location = new Location();
        this.location.setId(userVO.getLocationId());
        this.location.setName(userVO.getLocation());
        this.location.setTimezone(userVO.getTimeZone());
        this.department = new Department();
        this.department.setId(userVO.getDepartmentId());
        this.department.setName(userVO.getDepartment());
        this.firstName = userVO.getFirstName();
        this.lastName = userVO.getLastName();
        this.projectCode = userVO.getProjectCode();
        this.employeeId = userVO.getEmployeeId();
        this.managerId = userVO.getManagerId();
        this.email = userVO.getEmail();
        this.contactNo = userVO.getContactNo();
        this.organisation = new Organisation();
        this.account = new Account();
        this.account.setId(userVO.getAccountId());
        this.account.setAccountNo(userVO.getAccountNo());
        this.account.setName(userVO.getAccountName());
        this.organisation.setId(userVO.getOrganisationId());
    }

    @Transient
    @JsonView(Views.BasicView.class)
    public BigInteger getAccountId() {
        return (account != null) ? account.getId() : null;
    }
}