package com.centram.batch.config;

import com.centram.batch.router.IncidentAssign;
import com.centram.batch.router.IncidentSLANotification;
import org.apache.camel.CamelContext;
import org.apache.camel.component.quartz.QuartzComponent;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.util.concurrent.ThreadPoolRejectedPolicy;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

@Configuration
public class BatchConfig {

    private static final Logger LOG = LoggerFactory.getLogger(BatchConfig.class);

    @Value("${camel.thread.pool.name:integrator-camel-thread}")
    private String camelThreadPoolName;
    @Value("${camel.thread.pool.size:10}")
    private Integer camelThreadPoolSize;
    @Value("${camel.thread.pool.max-size:15}")
    private Integer camelThreadPoolMaxSize;
    @Value("${camel.thread.pool.max-queue-size:250}")
    private Integer camelThreadPoolMaxQueueSize;
    @Value("${camel.thread.pool.alive-time:2500000000000}")
    private Long camelThreadPoolAliveTime;
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Value("${camel.integrator.context-path}")
    private String contextPath;

    @Lazy
    @Bean
    public CamelContextConfiguration contextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext context) {
                ThreadPoolProfile threadPoolProfile = new ThreadPoolProfile();
                threadPoolProfile.setId(camelThreadPoolName);
                threadPoolProfile.setPoolSize(camelThreadPoolSize);
                threadPoolProfile.setMaxPoolSize(camelThreadPoolMaxSize);
                threadPoolProfile.setMaxQueueSize(camelThreadPoolMaxQueueSize);
                threadPoolProfile.setKeepAliveTime(camelThreadPoolAliveTime);
                threadPoolProfile.setRejectedPolicy(ThreadPoolRejectedPolicy.Abort);
                context.getExecutorServiceManager().registerThreadPoolProfile(threadPoolProfile);
            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {
                try {
                    //camelContext.addComponent("quartzComponent", quartzComponent(schedulerFactory));
                    //camelContext.addRoutes(incidentSLANotification);
                    //camelContext.addRoutes(incidentAssign);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Bean
    public SchedulerFactory schedulerFactory() throws SchedulerException {
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        schedulerFactory.initialize("default-quartz.properties");
        return schedulerFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(SchedulerFactory schedulerFactory) throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setSchedulerFactory(schedulerFactory);
        return schedulerFactoryBean;
    }

    @Bean("quartzProperties")
    @ConfigurationProperties(prefix = "app.batch")
    public Properties quartzProperties() {
        return new Properties();
    }

    @Bean
    public QuartzComponent quartzComponent(@Qualifier("quartzProperties") Properties properties) {
        QuartzComponent quartz = new QuartzComponent();
        quartz.setProperties(properties);
        //quartz.setSchedulerFactory(schedulerFactory);
        //quartz.setPropertiesFile("quartz.properties");
        return quartz;
    }

    @Bean
    public IncidentSLANotification incidentSLANotification() {
        return new IncidentSLANotification();
    }

    @Bean
    public IncidentAssign incidentAssign() {
        return new IncidentAssign();
    }

    /*@Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), contextPath);
        registration.setName("CamelServlet");
        return registration;
    }*/
}
