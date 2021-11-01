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
import java.util.stream.Collectors;

public class LoggedInUserDTO implements UserDetails, Serializable {

    private static final long serialVersionUID = 8129469224374551656L;

    private BigInteger userId;
    private BigInteger organisationId;
    private Boolean appManager;
    @JsonIgnore
    private String userName;
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

    public LoggedInUserDTO() {
    }

    public LoggedInUserDTO(UserVO userVO, HashMap<String, String> modulePermissions) {
        this.userId = userVO.getId();
        this.organisationId = (userVO.getOrganisationId() == null) ? null : userVO.getOrganisationId();
        this.userName = userVO.getUserName();
        this.password = userVO.getPassword();
        this.modulePermissions = modulePermissions;
        //this.authorities = Collections.singletonList(new SimpleGrantedAuthority(userVO.getRoleNames()));
        this.authorities = userVO.getRoleNames()
                .stream()
                .map(r-> new SimpleGrantedAuthority(r))
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        return this.getUserName();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoggedInUserDTO that = (LoggedInUserDTO) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (organisationId != null ? !organisationId.equals(that.organisationId) : that.organisationId != null)
            return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (authToken != null ? !authToken.equals(that.authToken) : that.authToken != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (authorities != null ? !authorities.equals(that.authorities) : that.authorities != null) return false;
        return modulePermissions != null ? modulePermissions.equals(that.modulePermissions) : that.modulePermissions == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (organisationId != null ? organisationId.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (authToken != null ? authToken.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (authorities != null ? authorities.hashCode() : 0);
        result = 31 * result + (modulePermissions != null ? modulePermissions.hashCode() : 0);
        return result;
    }
}
