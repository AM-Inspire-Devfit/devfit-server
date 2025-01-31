package com.amcamp.infra.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.data.redis")
public record RedisProperties(String host, int port, String password) {}
