package com.centram.common.config;

import lombok.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ConfigurationProperties(prefix = "spring.redis.cluster")
@ConditionalOnProperty(name = {"spring.redis.cluster.nodes"})
@Configuration
public class ClusterRedisConfig {
    private String nodes;
    private String password;
    private int maxRedirects;
}
