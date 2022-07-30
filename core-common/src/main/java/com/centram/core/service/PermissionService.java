package com.centram.core.service;


import com.centram.common.dto.PermissionDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.core.repository.PermissionRepository;
import com.centram.domain.Action;
import com.centram.domain.Module;
import com.centram.domain.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ActionService actionService;

    @Transactional(readOnly = false)
    public void save(PermissionDTO permissionDTO) {
        permissionRepository.deletePermissionByRoleAndMoule(permissionDTO.getRoleId(), permissionDTO.getModuleId());
        for (BigInteger actionId : permissionDTO.getActionIds()) {
            permissionRepository.savePermission(permissionDTO.getRoleId(), permissionDTO.getModuleId(), actionId);
        }
    }

    @Transactional(readOnly = true)
    public List<Module> getModulesByRole(BigInteger roleId) {
        List<Module> modules = permissionRepository.getModulesByRole(roleId);
        modules.stream()
                .forEach(i -> {
                    if (i.getParentModuleId() != null) {
                        i.setParentModuleName(moduleService.getModuleById(i.getParentModuleId()).getName());
                    }
                });
        return modules;
    }

    @Transactional(readOnly = true)
    public List<Action> getActionsByRoleAndModule(BigInteger roleId, BigInteger moduleId) {
        return permissionRepository.getActionsByRoleAndModule(roleId, moduleId);
    }

    @Transactional(readOnly = true)
    public List<Permission> getPermissionByRoleId(BigInteger roleId) {
        List<Permission> permissions = redisService.getPermissionByRoleId(roleId);
        if (permissions.size() == 0) {
            permissions = permissionRepository.getPermissionByRoleId(roleId);
            if (permissions != null) {
                redisService.savePermission(roleId, permissions);
            } else {
                throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
            }
        }
        return permissions;
    }

    @Transactional(readOnly = true)
    public List<Permission> getPermissionByRoleIds(List<BigInteger> roleIds) {
        List<Permission> permissions = new ArrayList<Permission>();
        for (BigInteger roleId : roleIds) {
            permissions.addAll(this.getPermissionByRoleId(roleId));
        }
        return permissions;
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
    //@Cacheable(cacheNames = "permission", key = "'permission_'.concat(#permissionId)")
    public Permission getPermissionById(BigInteger permissionId) {
        Permission permission = permissionRepository.getOne(permissionId);
        if (permission == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return permission;
    }
}
