package com.erp.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.erp.auth", "com.erp.common", "com.erp.domain"})
@EntityScan(basePackages = {"com.erp.domain"})
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = {"com.erp.auth.repository", "com.erp.common.repository"})
@SpringBootApplication(scanBasePackages = {"com.erp.auth", "com.erp.common", "com.erp.domain"})
public class AuthApp {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(AuthApp.class, args);
    }
}
