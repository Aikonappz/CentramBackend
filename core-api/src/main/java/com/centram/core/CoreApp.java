package com.centram.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

//@EnableDiscoveryClient
//@EnableFeignClients(basePackages = {"com.erp.api.client"})
@EnableSwagger2
@ComponentScan(basePackages = {
        "com.centram.core",
        "com.centram.core.config",
        "com.centram.common",
        "com.centram.common.config",
        "com.centram.domain"
})
@EntityScan(basePackages = {"com.centram.domain"})
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = {"com.centram.core.repository"})
@SpringBootApplication(scanBasePackages = {
        "com.centram.core",
        "com.centram.core.config",
        "com.centram.common",
        "com.centram.common.config",
        "com.centram.domain"
})
@EnableTransactionManagement
@EnableAutoConfiguration
@EnableCaching
@EnableSpringDataWebSupport
public class CoreApp {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CoreApp.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}