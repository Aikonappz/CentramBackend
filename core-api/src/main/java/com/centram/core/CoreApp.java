package com.centram.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.TimeZone;


@EnableCaching
@EnableAsync
@EnableSwagger2
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = {"com.centram.core.repository"})
@EnableTransactionManagement
@EnableAutoConfiguration
/*@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})*/
@ComponentScan(basePackages = {"com.centram.core", "com.centram.common", "com.centram.domain", "com.centram.batch"})
@EntityScan(basePackages = {"com.centram.domain"})
@SpringBootApplication(scanBasePackages = {"com.centram.core", "com.centram.common", "com.centram.domain", "com.centram.batch"})
public class CoreApp {

    @Value("${app.temp.path}")
    private String appTmpPath;

    @Value("${app.report.path}")
    private String appReportPath;

    @Value("${app.data-file.path}")
    private String appDataFilePath;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CoreApp.class, args);
    }

    @PostConstruct
    public void init() {
        this.makeDirectory(appDataFilePath);
        this.makeDirectory(appTmpPath);
        this.makeDirectory(appReportPath);
        //set system timestamp
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    /**
     * create directory and give permissions for app
     *
     * @param path
     */
    private void makeDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
            directory.setReadable(true, false);
            directory.setWritable(true, false);
            directory.setExecutable(true, false);
        }
    }
}