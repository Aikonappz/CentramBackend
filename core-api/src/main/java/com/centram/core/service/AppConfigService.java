package com.centram.core.service;


import com.centram.core.repository.AppConfigRepository;
import com.centram.domain.AppConfiguration;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppConfigService {

    private static final Logger log = LoggerFactory.getLogger(AppConfigService.class);

    @Autowired
    private AppConfigRepository appConfigRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "appConfiguration", key = "#configurationKey")
    public AppConfiguration findByConfigurationKeyAndStatus(String configurationKey) {
        AppConfiguration appConfiguration = appConfigRepository.findByConfigurationKeyAndStatus(configurationKey, Status.ACTIVE);
        return appConfiguration;
    }

    @Transactional(readOnly = true)
    public List<AppConfiguration> getAppConfigurations(List<String> configurationKeys) {
        return appConfigRepository.getAppConfigurations(configurationKeys);
    }
}
