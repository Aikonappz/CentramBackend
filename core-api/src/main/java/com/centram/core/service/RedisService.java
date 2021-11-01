package com.centram.core.service;

import com.centram.common.vo.UserVO;

import com.centram.domain.ActivityLog;
import com.centram.domain.Organisation;
import com.centram.domain.Role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class RedisService {

    public static final Logger log = LoggerFactory.getLogger(RedisService.class);

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
    /*ROLE*/
    @CachePut(value = "role", key = "#roleId")
    public Role redisOperation(BigInteger roleId, Role role) {
        return role;
    }

    @Cacheable(value = "role", key = "#roleId")
    public Role getCachedRole(BigInteger roleId) {
        return null;
    }
    /*ROLE*/

    /*ORGANISATION*/
    @CachePut(value = "organisationByMnemonic", key = "#mnemonic")
    public Organisation redisOperation(String mnemonic, Organisation organisation) {
        return organisation;
    }
    @Cacheable(value = "organisationByMnemonic", key = "#mnemonic")
    public Organisation getCachedOrganisation(String mnemonic) {
        return null;
    }
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
    public UserVO getCachedUser(String userName) {return null;}
    /*USER*/


}
