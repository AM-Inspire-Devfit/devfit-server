package com.amcamp.global.config.swagger;

import com.amcamp.global.common.constants.UrlConstants;
import com.amcamp.global.helper.SpringEnvironmentHelper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SwaggerConfig {

    private final SpringEnvironmentHelper springEnvironmentHelper;

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
        return List.of(new Server().url(getServerUrlByProfile()));
    }

    private String getServerUrlByProfile() {
        return switch (springEnvironmentHelper.getCurrentProfile()) {
            case "dev" -> UrlConstants.DEV_SERVER_URL;
            default -> UrlConstants.LOCAL_SERVER_URL;
        };
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

    @Profile({"dev", "local"})
    @Bean
    public UserDetailsService userDetailsService(
            @Value("${spring-doc.swagger-ui.username}") String username,
            @Value("${spring-doc.swagger-ui.password}") String password) {
        UserDetails user =
                User.withUsername(username).password("{noop}" + password).roles("SWAGGER").build();

        return new InMemoryUserDetailsManager(user);
    }
}
