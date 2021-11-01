package com.erp.auth.service;


import com.erp.auth.repository.PermissionRepository;
import com.erp.auth.repository.UserRepository;
import com.erp.common.dto.LoggedInUserDTO;
import com.erp.domain.Permission;
import com.erp.domain.User;
import com.erp.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public User save(User user) {
        return userRepository.save(user);
    }

    public void updateStatus(Status status, BigInteger userId) {
        userRepository.updateStatus(status, userId);
    }

    public User getUserById(BigInteger userId) {
        return userRepository.getOne(userId);
    }

    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> getUserByIds(List<BigInteger> ids, Pageable pageable) {
        return userRepository.getUserByIds(ids, pageable);
    }

    public User getUserByUserName(String userName) {
        return userRepository.getUserByUserName(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*User user = userRepository.getUserByUserName(username);
        if (user != null) {
            HashMap<String, HashMap<String, String>> modulePermissions = new HashMap<String, HashMap<String, String>>();
            HashMap<String, String> subModulePermissions = new HashMap<String, String>();
            List<Permission> permissions = permissionRepository.getPermissionByRoleIds(user.getRoles(), PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id"))).getContent();
            String prm = null;
            for (Permission permission : permissions) {
                prm = null;
                subModulePermissions = subModulePermissions = new HashMap<String, String>();
                if (modulePermissions.containsKey(permission.getModule().getName())) {
                    subModulePermissions = modulePermissions.get(permission.getModule().getName());
                    if (subModulePermissions.containsKey(permission.getSubmodule().getName())) {
                        prm = subModulePermissions.get(permission.getSubmodule().getName());
                        prm = prm.concat(",").concat(permission.getAction().getName());
                    } else {
                        prm = permission.getAction().getName();
                    }
                } else {
                    prm = permission.getAction().getName();
                }
                subModulePermissions.put(permission.getSubmodule().getName(),prm);
                modulePermissions.put(permission.getModule().getName(), subModulePermissions);
            }*/
            LoggedInUserDTO loggedInUserDTO = new LoggedInUserDTO();
            //redisTemplate.opsForValue().set(username, loggedInUserDTO);
            return loggedInUserDTO;
       /* } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }*/
    }
}
