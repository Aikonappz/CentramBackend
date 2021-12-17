package com.centram.core.repository;


import com.centram.domain.AppConfiguration;
import com.centram.domain.enumarator.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfiguration, BigInteger> {
    AppConfiguration findByConfigurationKeyAndStatus(@Param("configurationKey") String configurationKey, @Param("Status") Status status);

    @Query("select ac from AppConfiguration ac where ac.configurationKey in (:configurationKeys) and status = 1")
    List<AppConfiguration> getAppConfigurations(@Param("configurationKeys") List<String> configurationKeys);
}

