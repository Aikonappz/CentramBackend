package com.centram.common.vo;


import com.centram.domain.BaseEntity;
import com.centram.domain.User;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserVO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 9194601811170425900L;
    private BigInteger id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String password;
    private String contactNo;
    private String secContactNo;
    private String employeeId;
    private String timeZone;
    private BigInteger managerId;
    private String projectCode;
    private List<BigInteger> roles;
    private List<String> roleNames;
    private Status status;
    private BigInteger organisationId;
    private String organisation;
    private BigInteger locationId;
    private String location;
    private BigInteger departmentId;
    private String department;
    private LicenseType licenseType;


    public UserVO(User user) {
        super(user.getCreatedDate(), user.getModifiedDate(), user.getVersion(), user.getModifiedBy(), user.getCreatedBy());
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFirstName().concat(" ").concat(user.getLastName());
        this.email = user.getEmail();
        this.contactNo = user.getContactNo();
        this.secContactNo = user.getSecContactNo();
        this.employeeId = user.getEmployeeId();
        this.password = user.getPassword();
        this.projectCode = user.getProjectCode();
        this.locationId = (user.getLocation() != null) ? user.getLocation().getId() : null;
        this.departmentId = (user.getDepartment() != null) ? user.getDepartment().getId() : null;
        this.roles = user.getRoles();
        this.organisationId = (user.getOrganisation() != null) ? user.getOrganisation().getId() : null;
        this.status = user.getStatus();
        this.location = (user.getLocation() != null) ? user.getLocation().getName() : null;
        this.organisation = (user.getOrganisation() != null) ? user.getOrganisation().getName() : null;
        this.department = (user.getDepartment() != null) ? user.getDepartment().getName() : null;
        this.managerId = (user.getManagerId() != null) ? user.getManagerId() : null;
        this.timeZone = (user.getLocation() != null) ? user.getLocation().getTimezone() : "Asia/Kolkata";
        this.licenseType = (user.getOrganisation() != null) ? user.getOrganisation().getLicenseType() : null;
    }
}

