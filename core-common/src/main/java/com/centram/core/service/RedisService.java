package com.centram.core.service;

import com.centram.common.vo.UserVO;
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.LicenseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {

    public static final Logger log = LoggerFactory.getLogger(RedisService.class);

    /*ORGANISATION*/
    @CachePut(value = "organisation", key = "#organisationId")
    public Organisation saveOrganisation(BigInteger organisationId, Organisation organisation) {
        return organisation;
    }

    @Cacheable(value = "organisation", key = "#organisationId")
    public Organisation getOrganisationById(BigInteger organisationId) {
        return null;
    }
    /*ORGANISATION*/


    /*MODULE*/
    @CachePut(value = "module", key = "#moduleId")
    public Module saveModule(BigInteger moduleId, Module module) {
        return module;
    }

    @Cacheable(value = "module", key = "#moduleId")
    public Module getModuleById(BigInteger moduleId) {
        return null;
    }
    @CachePut(value = "module", key = "#p1 + #p2")
    public Module saveModuleByCustomerModuleName(LicenseType licenseType, String customerModuleName, Module module) {
        return module;
    }

    @Cacheable(value = "module", key = "#p1 + #p2")
    public Module getModuleByCustomerModuleName(LicenseType licenseType, String customerModuleName) {
        return null;
    }
    /*MODULE*/

    /*AppConfiguration*/
    @CachePut(value = "appConfiguration", key = "#appConfigurationKey")
    public AppConfiguration saveAppConfiguration(String appConfigurationKey, AppConfiguration appConfiguration) {
        return appConfiguration;
    }

    @Cacheable(value = "appConfiguration", key = "#appConfigurationKey")
    public AppConfiguration getAppConfigurationByKey(String appConfigurationKey) {
        return null;
    }
    /*AppConfiguration*/

    /*ROLE*/
    @CachePut(value = "role", key = "#roleId")
    public Role saveRoleById(BigInteger roleId, Role role) {
        return role;
    }

    @Cacheable(value = "role", key = "#roleId")
    public Role getRoleById(BigInteger roleId) {
        return null;
    }

    @CachePut(value = "role", key = "#roleName")
    public Role saveRoleByName(String roleName, Role role) {
        return role;
    }

    @Cacheable(value = "role", key = "#roleName")
    public Role getRoleByName(String roleName) {
        return null;
    }
    @CachePut(value = "role", key = "#displayName")
    public Role saveRoleByDisplayName(String displayName, Role role) {
        return role;
    }

    @Cacheable(value = "role", key = "#displayName")
    public Role getRoleByDisplayName(String displayName) {
        return null;
    }
    /*ROLE*/

    /*PERMISSION*/
    @CachePut(value = "permission", key = "'role_'.concat(#roleId)")
    public List<Permission> savePermission(BigInteger roleId, List<Permission> permissions) {
        return permissions;
    }

    @Cacheable(value = "permission", key = "'role_'.concat(#roleId)")
    public List<Permission> getPermissionByRoleId(BigInteger roleId) {
        return new ArrayList<Permission>();
    }
    /*PERMISSION*/

    /*ORGANISATION*/
    @CachePut(value = "organisation", key = "#organisationId")
    public Organisation redisOperation(BigInteger organisationId, Organisation organisation) {
        return organisation;
    }

    @Cacheable(value = "organisation", key = "#organisationId")
    public Organisation getCachedOrganisation(BigInteger organisationId) {
        return null;
    }
    /*ORGANISATION*/

    /*USER*/
    @CachePut(value = "user", key = "#userId")
    public UserVO redisOperation(BigInteger userId, UserVO userVO) {
        return userVO;
    }

    @Cacheable(value = "user", key = "#userId")
    public UserVO getCachedUser(BigInteger userId) {
        return null;
    }

    @CachePut(value = "userByName", key = "#userName")
    public UserVO redisOperation(String userName, UserVO userVO) {
        return userVO;
    }

    @Cacheable(value = "userByName", key = "#userName")
    public UserVO getCachedUser(String userName) {
        return null;
    }
    /*USER*/


}
