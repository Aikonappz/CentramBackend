package com.erp.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


/**
 * This brings up the com.erp.auth.service registry at the server.port. The
 */
@EnableEurekaServer
@SpringBootApplication(exclude = {GsonAutoConfiguration.class})
public class ErpRegistry {
    public static void main(String[] args) {
        SpringApplication.run(ErpRegistry.class, args);
    }
}