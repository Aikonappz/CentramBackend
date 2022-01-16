package com.centram.common.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/*@EnableSpringDataWebSupport
@EnableTransactionManagement
@EnableJpaRepositories(
        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class,
        entityManagerFactoryRef = "reportEntityManagerFactory",
        transactionManagerRef = "reportTransactionManager",
        basePackages = {"com.centram.report.repository"}
)
@Configuration*/
public class ReportDbConfig {

    /*@Bean("reportHikariConfig")
    @ConfigurationProperties(prefix = "report.spring.datasource.hikari")
    public HikariConfig reportHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "reportDataSource")
    public DataSource dataSource(@Qualifier("reportHikariConfig") HikariConfig reportHikariConfig) {
        return new HikariDataSource(reportHikariConfig);
    }

    @Bean(name = "reportEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean reportEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("reportDataSource") DataSource dataSource
    ) {
        return
                builder
                        .dataSource(dataSource)
                        .packages("com.centram.report.domain")
                        .persistenceUnit("report")
                        .build();
    }

    @Bean(name = "reportTransactionManager")
    public PlatformTransactionManager reportTransactionManager(
            @Qualifier("reportEntityManagerFactory") EntityManagerFactory reportEntityManagerFactory
    ) {
        return new JpaTransactionManager(reportEntityManagerFactory);
    }

    @Bean(name = "reportEntityManager")
    public EntityManager reportEntityManager(
            @Qualifier("reportEntityManagerFactory") EntityManagerFactory reportEntityManagerFactory
    ) {
        return reportEntityManagerFactory.createEntityManager();
    }*/
}