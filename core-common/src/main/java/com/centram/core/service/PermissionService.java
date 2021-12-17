package com.centram.core.service;


import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.core.repository.PermissionRepository;
import com.centram.domain.Permission;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
public class PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "all_permissions", key = "'list'")
    public Page<Permission> getPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Permission> getPermissionByRoleIds(List<BigInteger> roleIds, Pageable pageable) {
        return permissionRepository.getPermissionByRoleIds(roleIds, pageable);
    }

    @Transactional(readOnly = true)
    public List<BigInteger> getRoleIdsByModuleAndAction(List<BigInteger> moduleIds, String actionName) {
        return permissionRepository.getRoleIdsByModuleAndAction(moduleIds, actionName);
    }

    @Transactional(readOnly = true)
    public List<Permission> getPermissionByRoleNames(List<String> roleIds) {
        return permissionRepository.getPermissionByRoleNames(roleIds);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "permission", key = "'role_'.concat(#roleId)")
    public Page<Permission> getPermissionByRoleId(BigInteger roleId, Pageable pageable) {
        return permissionRepository.getPermissionByRoleId(roleId, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "permission", key = "'permission_'.concat(#permissionId)")
    public Permission getPermissionById(BigInteger permissionId) {
        Permission permission = permissionRepository.getOne(permissionId);
        if (permission == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return permission;
    }
}
