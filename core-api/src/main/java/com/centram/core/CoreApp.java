package com.centram.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EntityScan(basePackages = {"com.centram.domain"})
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = {"com.centram.core.repository"})
@EnableTransactionManagement
@EnableAutoConfiguration
/*@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})*/
@EnableSwagger2
@ComponentScan(basePackages = {"com.centram.core", "com.centram.common", "com.centram.domain", "com.centram.batch"})
@SpringBootApplication(scanBasePackages = {"com.centram.core", "com.centram.common", "com.centram.domain", "com.centram.batch"})
public class CoreApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CoreApp.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}