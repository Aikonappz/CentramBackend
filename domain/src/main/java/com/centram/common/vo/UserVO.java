package com.centram.common.vo;


import com.centram.common.view.Views;
import com.centram.domain.BaseEntity;
import com.centram.domain.User;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserVO extends BaseEntity implements Serializable, Comparable<UserVO> {
    private static final long serialVersionUID = 9194601811170425900L;
    @JsonView(Views.ThirdPartyView.class)
    private BigInteger id;
    @JsonView(Views.ThirdPartyView.class)
    private String firstName;
    @JsonView(Views.ThirdPartyView.class)
    private String lastName;
    private String fullName;
    @JsonView(Views.ThirdPartyView.class)
    private String email;
    private String password;
    @JsonView(Views.ThirdPartyView.class)
    private String contactNo;
    @JsonView(Views.ThirdPartyView.class)
    private String secContactNo;
    @JsonView(Views.ThirdPartyView.class)
    private String employeeId;
    @JsonView(Views.ThirdPartyView.class)
    private String timeZone;
    @JsonView(Views.ThirdPartyView.class)
    private String mngrId;
    @JsonView(Views.ThirdPartyView.class)
    private BigInteger managerId;
    @JsonView(Views.ThirdPartyView.class)
    private String projectCode;
    private List<BigInteger> roles;
    @JsonView(Views.ThirdPartyView.class)
    private List<String> roleNames;
    private List<String> roleViewNames;
    private Set<String> categories;
    private Set<String> subCategories;
    @JsonView(Views.ThirdPartyView.class)
    private String status;
    private BigInteger organisationId;
    private String organisation;
    @JsonView(Views.ThirdPartyView.class)
    private BigInteger locationId;
    @JsonView(Views.ThirdPartyView.class)
    private String location;
    private String locationOfficeName;
    @JsonView(Views.ThirdPartyView.class)
    private BigInteger departmentId;
    private BigInteger vendorId;
    private String vendor;
    @JsonView(Views.ThirdPartyView.class)
    private String department;
    private LicenseType licenseType;
    private BigInteger accountId;
    private String accountName;
    private String accountNo;

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
        this.status = user.getStatus().toString();
        this.location = (user.getLocation() != null) ? user.getLocation().getName() : null;
        this.locationOfficeName = (user.getLocation() != null) ? user.getLocation().getOfficeName() : null;
        this.organisation = (user.getOrganisation() != null) ? user.getOrganisation().getName() : null;
        this.department = (user.getDepartment() != null) ? user.getDepartment().getName() : null;
        this.managerId = (user.getManagerId() != null) ? user.getManagerId() : null;
        this.timeZone = (user.getLocation() != null) ? user.getLocation().getTimezone() : "Asia/Kolkata";
        this.licenseType = (user.getOrganisation() != null) ? user.getOrganisation().getLicenseType() : null;
        this.vendorId = (user.getVendor() != null) ? user.getVendor().getId() : null;
        this.accountId = (user.getAccount() != null) ? user.getAccount().getId() : null;
        this.accountName = (user.getAccount() != null) ? user.getAccount().getName() : null;
        this.accountNo = (user.getAccount() != null) ? user.getAccount().getAccountNo() : null;
        this.vendor = (user.getVendor() != null) ? user.getVendor().getName() : null;
    }

    @Override
    public int compareTo(@NotNull UserVO userVO) {
        return this.getFullName().compareTo(userVO.getFullName());
    }
}

