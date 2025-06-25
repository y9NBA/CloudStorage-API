package org.y9nba.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.y9nba.app.dto.response.ErrorResponse;
import org.y9nba.app.security.handler.CustomAccessDeniedHandler;
import org.y9nba.app.security.handler.CustomLogoutHandler;
import org.y9nba.app.security.handler.OAuth2LoginSuccessHandler;
import org.y9nba.app.security.jwt.JwtFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
                        .configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementCustomizer -> sessionManagementCustomizer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint((request, response, authException) ->
                                        unauthorizedResponse(response)
                                )
                                .accessDeniedHandler(
                                        customAccessDeniedHandler
                                )
                )
                .authorizeHttpRequests(auth -> {
                            auth.requestMatchers(
                                    "/auth/**",
                                    "/general/**",
                                    "/css/**",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/h2-console/**",
                                    "/sharing/view/**",
                                    "/sharing/download/**",
                                    "/user/search/avatar/**",
                                    "/confirm/**",
                                    "/recovery/**",
                                    "/oauth2/**"
                            ).permitAll();

                            auth.requestMatchers(
                                    "/admin/**"
                            ).hasAnyRole("ADMIN", "SUPER_ADMIN");

                            auth.requestMatchers(
                                    "/sharing/update/**",
                                    "/sharing/owner-files/**",
                                    "/storage/**",
                                    "/access/**",
                                    "/user/search/**"
                            ).hasAuthority("MANIPULATE_STORAGE");

                            auth.anyRequest().authenticated();
                        }
                )
                .oauth2Login(oauth2Login -> {
                    oauth2Login.successHandler(oAuth2LoginSuccessHandler);
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(log -> {
                    log.logoutUrl("/auth/logout");
                    log.addLogoutHandler(customLogoutHandler);
                    log.logoutSuccessHandler((request, response, authentication) ->
                            SecurityContextHolder.clearContext());
                })
                .headers(headersConfigurer ->
                        headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)    // headers для работы iframe тега у h2-console
                );

        return httpSecurity.build();
    }

    private void unauthorizedResponse(HttpServletResponse response) throws IOException {
        OutputStream responseOutputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(
                responseOutputStream,
                new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.name(),
                        HttpStatus.UNAUTHORIZED.value()
                )
        );
        responseOutputStream.flush();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000", "https://ggj-cldstrg.ru/"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
