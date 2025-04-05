package com.amcamp.global.config.security;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.security.config.Customizer.withDefaults;

import com.amcamp.domain.auth.application.JwtTokenService;
import com.amcamp.global.common.constants.UrlConstants;
import com.amcamp.global.helper.SpringEnvironmentHelper;
import com.amcamp.global.security.JwtAuthenticationFilter;
import com.amcamp.global.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final SpringEnvironmentHelper springEnvironmentHelper;
    private final JwtTokenService jwtTokenService;
    private final CookieUtil cookieUtil;

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/swagger-ui/**", "/v3/api-docs/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(withDefaults())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(
                auth ->
                        auth.requestMatchers("/devfit-actuator/**")
                                .permitAll()
                                .requestMatchers("/feedbacks/**")
                                .authenticated()
                                .requestMatchers("/**")
                                .permitAll()
                                .requestMatchers("/feedbacks/**")
                                .authenticated()
                                .anyRequest()
                                .authenticated());

        http.addFilterBefore(
                jwtAuthenticationFilter(jwtTokenService, cookieUtil),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        if (springEnvironmentHelper.isDevProfile()) {
            configuration.addAllowedOriginPattern(UrlConstants.DEV_SERVER_URL);
            configuration.addAllowedOriginPattern(UrlConstants.DEV_DOMAIN_URL);
            configuration.addAllowedOriginPattern(UrlConstants.LOCAL_DOMAIN_URL);
        }

        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.addExposedHeader(SET_COOKIE);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtTokenService jwtTokenService, CookieUtil cookieUtil) {
        return new JwtAuthenticationFilter(jwtTokenService, cookieUtil);
    }
}
