package com.whatshouldwedo.security.config;

import com.whatshouldwedo.core.constant.Constants;
import com.whatshouldwedo.core.utility.JsonWebTokenUtil;
import com.whatshouldwedo.security.filter.ExceptionFilter;
import com.whatshouldwedo.security.filter.GlobalLoggerFilter;
import com.whatshouldwedo.security.filter.JsonWebTokenAuthenticationFilter;
import com.whatshouldwedo.security.handler.common.DefaultAccessDeniedHandler;
import com.whatshouldwedo.security.handler.common.DefaultAuthenticationEntryPoint;
import com.whatshouldwedo.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final DefaultAccessDeniedHandler defaultAccessDeniedHandler;
    private final DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint;

    private final UserRepository userRepository;
    private final JsonWebTokenUtil jsonWebTokenUtil;

    @Value("${cookie.domain:localhost}")
    private String cookieDomain;

    @Value("${cookie.access-token-name:access_token}")
    private String accessTokenCookieName;

    @Value("${cookie.refresh-token-name:refresh_token}")
    private String refreshTokenCookieName;

    @Value("${jwt.refresh-token-validity-ms}")
    private Long cookieMaxAge;

    @Bean
    @Profile("!local")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(Constants.NO_NEED_AUTH_URLS.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.GET, Constants.NO_NEED_AUTH_GET_URLS.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(defaultAccessDeniedHandler)
                        .authenticationEntryPoint(defaultAuthenticationEntryPoint)
                )
                .addFilterBefore(
                        new JsonWebTokenAuthenticationFilter(
                                userRepository,
                                jsonWebTokenUtil,
                                cookieDomain,
                                accessTokenCookieName,
                                refreshTokenCookieName,
                                cookieMaxAge
                        ),
                        LogoutFilter.class
                )

                .addFilterBefore(
                        new ExceptionFilter(),
                        JsonWebTokenAuthenticationFilter.class
                )

                .addFilterBefore(
                        new GlobalLoggerFilter(),
                        ExceptionFilter.class
                )

                .getOrBuild();
    }

    @Bean
    @Profile("local")
    public SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {
                })
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(Constants.NO_NEED_AUTH_URLS.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.GET, Constants.NO_NEED_AUTH_GET_URLS.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(defaultAccessDeniedHandler)
                        .authenticationEntryPoint(defaultAuthenticationEntryPoint)
                )
                .addFilterBefore(
                        new JsonWebTokenAuthenticationFilter(
                                userRepository,
                                jsonWebTokenUtil,
                                cookieDomain,
                                accessTokenCookieName,
                                refreshTokenCookieName,
                                cookieMaxAge
                        ),
                        LogoutFilter.class
                )

                .addFilterBefore(
                        new ExceptionFilter(),
                        JsonWebTokenAuthenticationFilter.class
                )

                .addFilterBefore(
                        new GlobalLoggerFilter(),
                        ExceptionFilter.class
                )

                .getOrBuild();
    }
}
