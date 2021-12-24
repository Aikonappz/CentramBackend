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

import java.util.ArrayList;
import java.util.List;

@Service
public class AppConfigService {

    private static final Logger log = LoggerFactory.getLogger(AppConfigService.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private AppConfigRepository appConfigRepository;

    @Transactional(readOnly = true)
    public AppConfiguration findByConfigurationKeyAndStatus(String configurationKey) {
        AppConfiguration appConfiguration = redisService.getAppConfigurationByKey(configurationKey);
        if (appConfiguration == null) {
            appConfiguration = appConfigRepository.findByConfigurationKeyAndStatus(configurationKey, Status.ACTIVE);
            redisService.saveAppConfiguration(configurationKey, appConfiguration);
        }
        return appConfiguration;
    }

    public List<AppConfiguration> getAppConfigurations(List<String> configurationKeys) {
        List<AppConfiguration> appConfigurations = new ArrayList<AppConfiguration>();
        for (String key : configurationKeys) {
            appConfigurations.add(this.findByConfigurationKeyAndStatus(key));
        }
        return appConfigurations;
    }
}
