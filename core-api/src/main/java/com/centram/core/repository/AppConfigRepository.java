package com.centram.core.repository;


import com.centram.domain.AppConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfiguration, BigInteger> {
    AppConfiguration findByConfigurationKey(@Param("configurationKey") String configurationKey);
}