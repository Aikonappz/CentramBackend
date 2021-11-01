package com.erp.auth.service;

import com.erp.auth.repository.RoleRepository;
import com.erp.common.exeception.AppException;
import com.erp.common.exeception.GenericErrorCode;
import com.erp.domain.Role;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Transactional
@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    //@Cacheable(cacheNames = "role", key = "#roleId")
    public Role getById(BigInteger roleId) {
        Role role = roleRepository.getOne(roleId);
        if (role == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        //return modelMapper.map(role, new TypeToken<RoleVO>() { }.getType());
        return role;
    }

    //@Cacheable(cacheNames = "all_roles", key = "'list'")
    public Page<Role> getRoles(Pageable pageable) {
        //return modelMapper.map(roleRepository.findAll(pageable).getContent(), new TypeToken<List<RoleVO>>() { }.getType());
        return roleRepository.findAll(pageable);
    }

}
