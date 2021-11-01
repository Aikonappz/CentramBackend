package com.erp.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ErpConfiguration {
    public static void main(String[] args) {
        SpringApplication.run(ErpConfiguration.class, args);
    }

   /* @Configuration
    public class ActuatorSecurityConfigurer extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(HttpSecurity http) throws Exception {


            http.csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("/**")
                    .permitAll()
                    .and()
                    .httpBasic();

        }
    }*/

}
