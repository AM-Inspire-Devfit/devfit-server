package com.amcamp.infra.config.properties;

import com.amcamp.infra.config.oauth.GoogleProperties;
import com.amcamp.infra.config.oauth.KakaoProperties;
import com.amcamp.infra.config.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
        RedisProperties.class,
        GoogleProperties.class,
        KakaoProperties.class,
})
@Configuration
public class PropertiesConfig {}
