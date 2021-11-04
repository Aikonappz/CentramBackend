package com.centram.common.dto;


import com.centram.common.vo.UserVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoggedInUser implements UserDetails, Serializable {

    private static final long serialVersionUID = 8129469224374551656L;

    private BigInteger userId;
    private BigInteger organisationId;
    private BigInteger locationId;
    private BigInteger departmentId;
    private Boolean appManager;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String authToken;
    @JsonIgnore
    private String password;
    @JsonProperty("authorities")
    private Collection<? extends GrantedAuthority> authorities;


    //@JsonSerialize(using= CustomMapSerializer.class)
    //@JsonDeserialize(using= CustomMapDeserializer.class)
    @JsonProperty("modulePermissions")
    private HashMap<String, String> modulePermissions;

    public LoggedInUser() {
    }

    public LoggedInUser(UserVO userVO, HashMap<String, String> modulePermissions) {
        this.userId = userVO.getId();
        this.organisationId = (userVO.getOrganisationId() == null) ? null : userVO.getOrganisationId();
        this.locationId = (userVO.getLocationId() == null) ? null : userVO.getLocationId();
        this.departmentId = (userVO.getDepartmentId() == null) ? null : userVO.getDepartmentId();
        this.email = userVO.getEmail();
        this.password = userVO.getPassword();
        this.modulePermissions = modulePermissions;
        //this.authorities = Collections.singletonList(new SimpleGrantedAuthority(userVO.getRoleNames()));
        this.authorities = userVO.getRoleNames()
                .stream()
                .map(r -> new SimpleGrantedAuthority(r))
                .collect(Collectors.toList());
        this.appManager = userVO.getOrganisationId() == null;
    }

    public Boolean getAppManager() {
        return appManager;
    }

    public void setAppManager(Boolean appManager) {
        this.appManager = appManager;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(BigInteger organisationId) {
        this.organisationId = organisationId;
    }

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

    public HashMap<String, String> getModulePermissions() {
        return modulePermissions;
    }

    public void setModulePermissions(HashMap<String, String> modulePermissions) {
        this.modulePermissions = modulePermissions;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public BigInteger getLocationId() {
        return locationId;
    }

    public void setLocationId(BigInteger locationId) {
        this.locationId = locationId;
    }

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
