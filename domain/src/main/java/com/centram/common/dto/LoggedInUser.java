package com.centram.common.dto;


import com.centram.common.vo.PermissionVO;
import com.centram.common.vo.UserVO;
import com.centram.domain.enumarator.LicenseType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoggedInUser implements UserDetails, Serializable {

    private static final long serialVersionUID = 8129469224374551656L;

    private BigInteger userId;
    private BigInteger organisationId;
    private BigInteger locationId;
    private BigInteger departmentId;
    @JsonProperty("appManager")
    private Boolean appManager;
    private String name;
    private String orgName;
    private String timeZone;
    private String location;
    private String department;
    private String email;
    private String authToken;
    private String password;
    private List<BigInteger> roles;
    @JsonProperty("authorities")
    private Collection<? extends GrantedAuthority> authorities;
    @JsonProperty("roles")
    private List<PermissionVO> modulePermissions;
    @JsonProperty("licenseType")
    private LicenseType licenseType;
    @JsonProperty("authId")
    private BigInteger userAuthId;
    private String locationOpsTime;


    public LoggedInUser() {
    }

    public LoggedInUser(UserVO userVO, List<PermissionVO> modulePermissions) {
        this.userAuthId = null;
        this.userId = userVO.getId();
        this.organisationId = (userVO.getOrganisationId() == null) ? null : userVO.getOrganisationId();
        this.locationId = (userVO.getLocationId() == null) ? null : userVO.getLocationId();
        this.departmentId = (userVO.getDepartmentId() == null) ? null : userVO.getDepartmentId();
        this.name = userVO.getFirstName() + " " + userVO.getLastName();
        this.orgName = (userVO.getDepartmentId() == null) ? null : userVO.getOrganisation();
        this.email = userVO.getEmail();
        this.password = userVO.getPassword();
        this.modulePermissions = modulePermissions;
        this.timeZone = userVO.getTimeZone();
        this.location = userVO.getLocation();
        this.department = userVO.getDepartment();
        this.roles = userVO.getRoles();
        this.authorities = userVO.getRoleNames()
                .stream()
                .map(r -> new SimpleGrantedAuthority(r))
                .collect(Collectors.toList());
        this.appManager = userVO.getOrganisationId() == null;
        this.licenseType = (userVO.getDepartmentId() == null) ? null : userVO.getLicenseType();
        this.locationOpsTime = userVO.getLocationOpsTime();
    }

    public String getLocationOpsTime() {
        return locationOpsTime;
    }

    public BigInteger getUserAuthId() {
        return userAuthId;
    }

    public void setUserAuthId(BigInteger userAuthId) {
        this.userAuthId = userAuthId;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public List<BigInteger> getRoles() {
        return roles;
    }

    public void setRoles(List<BigInteger> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @JsonIgnore
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @JsonIgnore
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Boolean getAppManager() {
        return appManager;
    }

    public void setAppManager(Boolean appManager) {
        this.appManager = appManager;
    }

    @JsonIgnore
    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    @JsonIgnore
    public BigInteger getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(BigInteger organisationId) {
        this.organisationId = organisationId;
    }

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @JsonIgnore
    public List<PermissionVO> getModulePermissions() {
        return modulePermissions;
    }

    public void setModulePermissions(List<PermissionVO> modulePermissions) {
        this.modulePermissions = modulePermissions;
    }

    @JsonIgnore
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @JsonIgnore
    public BigInteger getLocationId() {
        return locationId;
    }

    public void setLocationId(BigInteger locationId) {
        this.locationId = locationId;
    }

    @JsonIgnore
    public BigInteger getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(BigInteger departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoggedInUser)) return false;
        LoggedInUser that = (LoggedInUser) o;
        return Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getOrganisationId(), that.getOrganisationId()) && Objects.equals(getLocationId(), that.getLocationId()) && Objects.equals(getDepartmentId(), that.getDepartmentId()) && Objects.equals(getAppManager(), that.getAppManager()) && Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getAuthToken(), that.getAuthToken()) && Objects.equals(getPassword(), that.getPassword()) && Objects.equals(getAuthorities(), that.getAuthorities()) && Objects.equals(getModulePermissions(), that.getModulePermissions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getOrganisationId(), getLocationId(), getDepartmentId(), getAppManager(), getEmail(), getAuthToken(), getPassword(), getAuthorities(), getModulePermissions());
    }
}