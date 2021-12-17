package com.centram.batch.config;

import com.centram.batch.router.IncidentRouter;
import org.apache.camel.CamelContext;
import org.apache.camel.component.quartz.QuartzComponent;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.util.concurrent.ThreadPoolRejectedPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @Value("${date.time.format:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}")
    private String dateTimeFormat;
    @Value("${date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Value("${camel.integrator.context-path}")
    private String contextPath;

    @Bean
    public CamelContextConfiguration contextConfiguration(IncidentRouter incidentRouter, QuartzComponent quartzComponent) {
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
                    camelContext.addComponent("quartzComponent", quartzComponent);
                    camelContext.addRoutes(incidentRouter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Bean
    public QuartzComponent quartzComponent() {
        QuartzComponent quartz = new QuartzComponent();
        quartz.setPropertiesFile("quartz.properties");
        return quartz;
    }

    @Bean
    public IncidentRouter incidentRouter() {
        return new IncidentRouter();
    }

    /*@Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), contextPath);
        registration.setName("CamelServlet");
        return registration;
    }*/
}
