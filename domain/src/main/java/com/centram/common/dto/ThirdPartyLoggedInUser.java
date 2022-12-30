package com.centram.common.dto;


import com.centram.domain.Organisation;
import com.centram.domain.enumarator.LicenseType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

public class ThirdPartyLoggedInUser extends UsernamePasswordAuthenticationToken implements Serializable {
    private static final long serialVersionUID = 5155394952014013427L;
    private BigInteger id;
    private String username;
    private String password;
    private LicenseType licenseType;

    public ThirdPartyLoggedInUser(Organisation organisation) {
        super(organisation.getCentramKey(), organisation.getCentramPass(), new ArrayList<GrantedAuthority>() {{
            add(new SimpleGrantedAuthority("THIRD_PARTY_USER"));
        }});
        this.id = organisation.getId();
        this.username = organisation.getCentramKey();
        this.password = organisation.getCentramPass();
        this.licenseType = organisation.getLicenseType();
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getUsername() {
        return this.getUsername();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThirdPartyLoggedInUser)) return false;
        ThirdPartyLoggedInUser that = (ThirdPartyLoggedInUser) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getPassword(), that.getPassword()) && getLicenseType() == that.getLicenseType() && Objects.equals(getAuthorities(), that.getAuthorities());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getPassword(), getLicenseType(), getAuthorities());
    }
}