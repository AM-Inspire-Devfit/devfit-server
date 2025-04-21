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
        return requestTemplate -> {
            if (requestTemplate.url().contains("kakao")
                    || requestTemplate.url().contains("google")) {
                requestTemplate.header(
                        "Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

                if (requestTemplate.body() == null) {
                    requestTemplate.body("");
                }
            }

            if (requestTemplate.url().contains("openai")) {
                requestTemplate.header("Content-Type", "application/json");
            }
        };
    }
}
