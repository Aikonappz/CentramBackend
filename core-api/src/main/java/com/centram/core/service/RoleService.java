package com.centram.core.service;


import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.RoleRepository;
import com.centram.domain.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    //@Cacheable(value = "role", key = "#roleId")
    public Role getById(BigInteger roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<String> getByIds(List<BigInteger> roleIds) {
        return roleRepository.findAllById(roleIds).stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    /**
     * @param roles
     * @return
     */
    @Transactional(readOnly = true)
    public List<Role> getByRoleNames(List<String> roles) {
        return roleRepository.getByRoleNames(roles);
    }

    @Transactional(readOnly = true)
    public PaginatedList<Role> getRoles(Pageable pageable) {
        return new PaginatedList<Role>(roleRepository.findAll(pageable));
    }
}
