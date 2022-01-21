package com.centram.core.service;


import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.ModuleRepository;
import com.centram.domain.Module;
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

@Transactional
@Service
public class ModuleService {
    private static final Logger log = LoggerFactory.getLogger(ModuleService.class);

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private RedisService redisService;

    @Transactional(readOnly = true)
    public PaginatedList<Module> getModules(Pageable pageable) {
        return new PaginatedList<Module>(moduleRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Module getModuleById(BigInteger moduleId) {
        Module module = redisService.getModuleById(moduleId);
        if (module == null) {
            Optional<Module> moduleOptional = moduleRepository.findById(moduleId);
            if (moduleOptional.isPresent()) {
                module = moduleOptional.get();
                redisService.saveModule(moduleId, moduleOptional.get());
            } else {
                throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
            }
        }
        return module;
    }

    @Transactional(readOnly = true)
    public List<Module> getModuleByIds(List<BigInteger> moduleIds) {
        List<Module> modules = new ArrayList<Module>();
        for (BigInteger moduleId : moduleIds) {
            modules.add(this.getModuleById(moduleId));
        }
        return modules;
    }

    @Transactional(readOnly = true)
    public List<Module> getCategories() {
        return moduleRepository.getCategories();
    }

    @Transactional(readOnly = true)
    public List<Module> getSubCategories(BigInteger parentModuleId) {
        return moduleRepository.getSubCategories(parentModuleId);
    }
}
