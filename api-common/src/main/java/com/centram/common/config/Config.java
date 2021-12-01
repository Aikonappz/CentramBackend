package com.centram.common.config;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.filter.RestFilter;
import com.centram.common.interceptor.RestEndPointInterceptor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class Config implements AsyncConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    @Value("${jasypt.encryptor.algorithm}")
    public String jasyptEncryptorAlgorithm;

    @Value("${jasypt.encryptor.password}")
    public String jasyptEncryptorPassword;

    @Value("${date.time.format:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}")
    private String dateTimeFormat;

    @Value("${date.format:yyyy-MM-dd}")
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
        };
    }

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
                }).build();
    }

    @Bean
    public TerminateBean getTerminateBean() {
        return new TerminateBean();
    }

    @Bean
    protected JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHostName, redisPort);
        //configuration.setPassword(redisPassword);
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().build();
        JedisConnectionFactory factory = new JedisConnectionFactory(configuration, jedisClientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }

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

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean("stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

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

    @Bean
    public StandardPBEStringEncryptor standardPBEStringEncryptor() {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setAlgorithm(jasyptEncryptorAlgorithm);
        standardPBEStringEncryptor.setPassword(jasyptEncryptorPassword);
        return standardPBEStringEncryptor;
    }

    @Bean
    public FilterRegistrationBean<RestFilter> restFilter() {
        FilterRegistrationBean<RestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RestFilter(appAllowedOrigins));
        registrationBean.addUrlPatterns("/*");
        //registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(600);
        executor.setThreadNamePrefix("AsynchThread-");
        executor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

    @Bean
    public AuditorAware<BigInteger> auditorAware() {
        return new AuditorAwareImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private abstract class IgnoreHibernatePropertiesInJackson {
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
            LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (loggedInUser == null) {
                return null;
            }
            return Optional.of(loggedInUser.getUserId());
        }
    }
}
