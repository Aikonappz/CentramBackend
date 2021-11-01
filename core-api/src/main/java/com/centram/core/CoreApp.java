package com.centram.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@EnableDiscoveryClient
//@EnableFeignClients(basePackages = {"com.erp.api.client"})
@EnableSwagger2
@ComponentScan(basePackages = {"com.centram.core", "com.centram.common", "com.centram.domain"})
@EntityScan(basePackages = {"com.centram.domain"})
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = {"com.centram.core.repository"})
@SpringBootApplication(scanBasePackages = {"com.centram.core", "com.centram.common", "com.centram.domain"})
public class CoreApp {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CoreApp.class, args);
    }
}