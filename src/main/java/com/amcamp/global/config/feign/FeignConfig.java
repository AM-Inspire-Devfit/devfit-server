package com.amcamp.global.config.feign;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.amcamp.infra.config.feign")
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
    }
}
