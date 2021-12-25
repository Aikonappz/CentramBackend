package com.centram.core.service;

import com.centram.common.vo.UserVO;
import com.centram.domain.Module;
import com.centram.domain.*;
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
    public Role saveRole(BigInteger roleId, Role role) {
        return role;
    }

    @Cacheable(value = "role", key = "#roleId")
    public Role getRoleById(BigInteger roleId) {
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

    /*ACTIVITY LOG*/
    @CachePut(value = "activeLogs", key = "#userId")
    public List<ActivityLog> redisOperation(BigInteger userId, List<ActivityLog> activityLogs) {
        return activityLogs;
    }

    @Cacheable(value = "activeLogs", key = "#userId")
    public List<ActivityLog> getCachedActivityLog(BigInteger userId) {
        return null;
    }
    /*ACTIVITY LOG*/


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
