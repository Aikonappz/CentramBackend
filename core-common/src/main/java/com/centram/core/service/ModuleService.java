package com.centram.core.service;


import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.CategoryLocationVO;
import com.centram.core.repository.ModuleRepository;
import com.centram.domain.Module;
import com.centram.domain.enumarator.LicenseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public PaginatedList<Module> getModules(String licenseType, BigInteger organisationId, Pageable pageable) {
        Page<Module> modulePage = moduleRepository.findAll(licenseType, organisationId, pageable);
        modulePage.getContent().stream().forEach(i -> {
            if (i.getParentModuleId() != null) {
                Module m = this.getModuleById(i.getParentModuleId());
                i.setParentModuleName(m.getCustomerModuleName() == null || m.getCustomerModuleName().equals("") ? m.getName() : m.getCustomerModuleName());
            }
        });
        return new PaginatedList<Module>(modulePage);
    }

    @Transactional(readOnly = true)
    public Module getModule(BigInteger moduleId) {
        Optional<Module> moduleOptional = moduleRepository.findById(moduleId);
        if (moduleOptional.isPresent()) {
            return moduleOptional.get();
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
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
    public Module getModuleByCustomerModuleName(LicenseType licenseType, String customerModuleName) {
        Module module = redisService.getModuleByCustomerModuleName(licenseType, customerModuleName);
        if (module == null) {
            module = moduleRepository.findByLicenseTypeAndCustomerModuleNameIgnoreCase(licenseType, customerModuleName);
            if (module != null) {
                redisService.saveModuleByCustomerModuleName(licenseType, customerModuleName, module);
            } else {
                return null;
            }
        }
        return module;
    }

    @Transactional(readOnly = true)
    public Module getSubModuleByCustomerModuleNameAndParentModuleId(BigInteger parentModuleId, String customerModuleName) {
        Module module = moduleRepository.getSubModuleByCustomerModuleNameAndParentModuleId(parentModuleId, customerModuleName);
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

    @Transactional(readOnly = true)
    public List<CategoryLocationVO> getCategorySubCategoriesAndLocation(BigInteger organisationId) {
        return moduleRepository.getCategorySubCategoriesAndLocation(organisationId);
    }
}
