package com.centram.common.config;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.ThirdPartyLoggedInUser;
import com.centram.common.filter.RestFilter;
import com.centram.common.interceptor.MdcInterceptor;
import com.centram.common.interceptor.RestEndPointInterceptor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;


@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Configuration
public class Config implements AsyncConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    @Value("${jasypt.encryptor.algorithm}")
    public String jasyptEncryptorAlgorithm;

    @Value("${jasypt.encryptor.password}")
    public String jasyptEncryptorPassword;

    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Value("${spring.redis.host}")
    private String redisHostName;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Value("${app.allowed.origins}")
    private String[] appAllowedOrigins;

    /*@Value("${spring.redis.password}")
    private String redisPassword;*/

    @Value("${spring.application.name}")
    private String appName;

    @Autowired(required = false)
    private StandaloneRedisConfig standaloneRedisConfig;

    @Autowired(required = false)
    private ClusterRedisConfig clusterRedisConfig;

    /**
     * jedis config
     *
     * @return
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = null;
        if (standaloneRedisConfig != null) {
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(standaloneRedisConfig.getHost(), standaloneRedisConfig.getPort());
            //configuration.setPassword(standaloneRedisConfig.getPassword());
            factory = new JedisConnectionFactory(configuration);

            return factory;
        }
        if (clusterRedisConfig != null) {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(Arrays.asList(clusterRedisConfig.getNodes().split(",")));
            redisClusterConfiguration.setMaxRedirects(clusterRedisConfig.getMaxRedirects());
            redisClusterConfiguration.setPassword(clusterRedisConfig.getPassword());
            factory = new JedisConnectionFactory(redisClusterConfiguration, jedisPoolConfig);
        }
        factory.afterPropertiesSet();
        return factory;
    }

    /**
     * redis config
     *
     * @param factory
     * @param objectMapper
     * @return
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory factory, ObjectMapper objectMapper) {
        RedisSerializationContext.SerializationPair<Object> jsonSerializer = RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(factory)
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig()
                        //.entryTtl(Duration.ofDays(1))
                        //.serializeValuesWith(jsonSerializer)
                )
                .build();
    }

    /**
     * CORS config
     *
     * @return
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "OPTION", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .maxAge(3600)
                        .allowedOrigins("*");
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new MdcInterceptor());
            }
        };
    }

    /**
     * jackson ObjectMapper config
     *
     * @return
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .simpleDateFormat(dateFormat)
                //.failOnEmptyBeans(false)
                .serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)))
                .serializerByType(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override
                    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat).withZone(ZoneId.systemDefault());
                        String s = localDateTime.atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
                        jsonGenerator.writeString(s);
                    }
                })
                .deserializerByType(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat).withZone(ZoneId.systemDefault());
                        String str = jsonParser.getText();
                        LocalDateTime localDateTime = null;
                        try {
                            localDateTime = LocalDateTime.parse(str, DATE_TIME_FORMATTER);
                        } catch (DateTimeParseException e) {
                            e.printStackTrace();
                        }
                        return localDateTime;
                    }
                })
                .serializerByType(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
                    @Override
                    public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat);
                        try {
                            String s = zonedDateTime.format(DATE_TIME_FORMATTER);
                            jsonGenerator.writeString(s);
                        } catch (DateTimeParseException e) {
                            System.err.println(e);
                            jsonGenerator.writeString("");
                        }
                    }
                })
                .deserializerByType(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
                    @Override
                    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                        String str = jsonParser.getText();
                        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat);
                        try {
                            return ZonedDateTime.parse(str, DATE_TIME_FORMATTER);
                        } catch (DateTimeParseException e) {
                            System.err.println(e);
                            return null;
                        }
                    }
                }).build()
                .registerModule(new JavaTimeModule());
    }

    /**
     * pre destroy hook
     *
     * @return
     */
    @Bean
    public TerminateBean getTerminateBean() {
        return new TerminateBean();
    }

    /**
     * ModelMapper config
     *
     * @return
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Redis Object template config
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        return template;
    }

    /**
     * String redis template
     *
     * @param factory
     * @return
     */
    @Bean("stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * RestTemplate config
     *
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new RestEndPointInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    /**
     * custom filter config
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<RestFilter> restFilter() {
        FilterRegistrationBean<RestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RestFilter(appAllowedOrigins));
        registrationBean.addUrlPatterns("/*");
        //registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    /**
     * asyncExecutor config
     *
     * @return
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(90);
        executor.setQueueCapacity(600);
        executor.setThreadNamePrefix("AsynchThread-asyncExecutor-");
        executor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

    /**
     *
     * @return
     */
    @Bean(name = "delayedExecutor")
    public Executor delayedExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(90);
        executor.setQueueCapacity(600);
        executor.setThreadNamePrefix("AsynchThread-delayedExecutor-");
        executor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

    /**
     * envers config
     *
     * @return
     */
    @Bean
    public AuditorAware<BigInteger> auditorAware() {
        return new AuditorAwareImpl();
    }

    /**
     * password encode config
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    class TerminateBean {
        @PreDestroy
        public void onDestroy() {
            LOG.info("{} shutting down...", appName);

        }
    }

    public class AuditorAwareImpl implements AuditorAware<BigInteger> {
        @Override
        public Optional<BigInteger> getCurrentAuditor() {
            Object user = SecurityContextHolder.getContext().getAuthentication();
            if (user == null) {
                //TODO : for batch need to handle later on
                return null;
            } else if (user != null && user instanceof ThirdPartyLoggedInUser) {
                //TODO : for batch need to handle later on
                return null;
            } else if (user != null && user instanceof LoggedInUser) {
                LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (loggedInUser == null) {
                    return null;
                }
                return Optional.of(loggedInUser.getUserId());
            } else {
                return null;
            }
        }
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private abstract class IgnoreHibernatePropertiesInJackson {
    }
}
