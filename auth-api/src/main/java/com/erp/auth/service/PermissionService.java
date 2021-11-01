package com.erp.auth.service;


import com.erp.auth.repository.PermissionRepository;
import com.erp.common.exeception.AppException;
import com.erp.common.exeception.GenericErrorCode;
import com.erp.domain.Permission;
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

@Transactional
@Service
public class PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Cacheable(cacheNames = "all_permissions", key = "'list'")
    public Page<Permission> getPermissions(Pageable pageable) {
        //return modelMapper.map(permissionRepository.findAll(pageable).getContent(), new TypeToken<List<PermissionVO>>() { }.getType());
        return permissionRepository.findAll(pageable);
    }

    //@Cacheable(cacheNames = "permission", key = "'role_'.concat(#roleId)")
    public Page<Permission> getPermissionByRoleId(BigInteger roleId, Pageable pageable) {
        //return modelMapper.map(permissionRepository.getPermissionByRoleId(roleId, pageable).getContent(), new TypeToken<List<PermissionVO>>() { }.getType());
        return permissionRepository.getPermissionByRoleId(roleId, pageable);
    }

    //@Cacheable(cacheNames = "permission", key = "'permission_'.concat(#permissionId)")
    public Permission getPermissionById(BigInteger permissionId) {
        Permission permission = permissionRepository.getOne(permissionId);
        if (permission == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        //return modelMapper.map(permission, new TypeToken<PermissionVO>() { }.getType());
        return permission;
    }
}
