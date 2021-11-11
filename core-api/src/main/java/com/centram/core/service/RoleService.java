package com.centram.core.service;


import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.RoleRepository;
import com.centram.domain.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;


@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "#roleId")
    public Role getById(BigInteger roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public PaginatedList<Role> getRoles(Pageable pageable) {
        return new PaginatedList<Role>(roleRepository.findAll(pageable));
    }
}
