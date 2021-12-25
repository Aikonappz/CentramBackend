package com.centram.core.service;


import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.RoleRepository;
import com.centram.domain.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RedisService redisService;

    /**
     * @param roleId
     * @return
     */
    @Transactional(readOnly = true)
    public Role getById(BigInteger roleId) {
        Role role = redisService.getRoleById(roleId);
        if (role == null) {
            Optional<Role> optionalRole = roleRepository.findById(roleId);
            if (optionalRole.isPresent()) {
                role = optionalRole.get();
                redisService.saveRole(roleId, role);
            } else {
                throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
            }
        }
        return role;
    }

    /**
     * @param roleIds
     * @return
     */
    @Transactional(readOnly = true)
    public List<String> getByIds(List<BigInteger> roleIds) {
        List<String> roleNames = new ArrayList<String>();
        Role role = null;
        for (BigInteger roleId : roleIds) {
            role = this.getById(roleId);
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    /**
     * @param roles
     * @return
     */
    @Transactional(readOnly = true)
    public List<Role> getByRoleNames(List<String> roles) {
        return roleRepository.getByRoleNames(roles);
    }

    /**
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Role> getRoles(Pageable pageable) {
        return new PaginatedList<Role>(roleRepository.findAll(pageable));
    }
}
