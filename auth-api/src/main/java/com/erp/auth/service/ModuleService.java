package com.erp.auth.service;


import com.erp.auth.repository.ModuleRepository;
import com.erp.common.exeception.AppException;
import com.erp.common.exeception.GenericErrorCode;
import com.erp.common.redis.repository.RedisModuleRepository;
import com.erp.domain.Module;
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
public class ModuleService {
    private static final Logger log = LoggerFactory.getLogger(ModuleService.class);

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private RedisModuleRepository redisModuleRepository;

    @Autowired
    private ModelMapper modelMapper;

    //@Cacheable(cacheNames = "all_modules", key = "'list'")
    public Page<Module> getModules(Pageable pageable) {
        //return modelMapper.map(moduleRepository.findAll(pageable).getContent(), new TypeToken<List<ModuleVO>>() {}.getType());
        return moduleRepository.findAll(pageable);
    }

    //@Cacheable(cacheNames = "module", key = "#moduleId")
    public Module getModuleById(BigInteger moduleId) {
        Module module = moduleRepository.getOne(moduleId);
        if (module == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        //return modelMapper.map(module, new TypeToken<ModuleVO>() { }.getType());
        return module;
    }
}
