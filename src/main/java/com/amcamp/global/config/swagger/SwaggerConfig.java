package com.amcamp.global.config.swagger;

import com.amcamp.global.common.constants.UrlConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SwaggerConfig {

    private final Environment environment;

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group("v1").pathsToMatch("/**").build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("DevFit Server API")
                                .description("DevFit Server API 명세서입니다.")
                                .version("v0.0.1"))
                .servers(getSwaggerServers())
                .components(authSetting())
                .addSecurityItem(securityRequirement());
    }

    private List<Server> getSwaggerServers() {
        return List.of(new Server().url(resolveServerUrl()));
    }

    private String resolveServerUrl() {
        List<String> profiles = List.of(environment.getActiveProfiles());

        if (profiles.contains("dev")) {
            return UrlConstants.DEV_SERVER_URL;
        }

        return UrlConstants.LOCAL_SERVER_URL;
    }

    private Components authSetting() {
        return new Components()
                .addSecuritySchemes(
                        "accessToken",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"));
    }

    private SecurityRequirement securityRequirement() {
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("accessToken");
        return securityRequirement;
    }
}
