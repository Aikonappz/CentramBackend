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
            module = moduleRepository.getById(moduleId);
            if (module != null) {
                redisService.saveModule(moduleId, module);
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
}
