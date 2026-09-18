package ru.emyxar.declarative_security_spring_boot_starter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.emyxar.declarative_security_spring_boot_starter.properties.Rule;
import ru.emyxar.declarative_security_spring_boot_starter.exceptions.internal.DefaultSecurityErrorResponse;
import ru.emyxar.declarative_security_spring_boot_starter.exceptions.StarterSecurityErrorResponseFactory;
import ru.emyxar.declarative_security_spring_boot_starter.exceptions.handlers.StarterAccessDeniedHandler;
import ru.emyxar.declarative_security_spring_boot_starter.exceptions.handlers.StarterAuthenticationEntryPoint;
import ru.emyxar.declarative_security_spring_boot_starter.filter.StarterTokenFilter;
import ru.emyxar.declarative_security_spring_boot_starter.properties.SecurityProperties;
import tools.jackson.databind.ObjectMapper;


@AutoConfiguration(before = {
        org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration.class
})
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(StarterSecurityErrorResponseFactory.class)
    public StarterSecurityErrorResponseFactory securityErrorResponseFactory() {
        return new DefaultSecurityErrorResponse();
    }

    @Bean
    @ConditionalOnMissingBean(StarterTokenFilter.class)
    public StarterTokenFilter tokenFilter() {
        return new StarterTokenFilter();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(
            ObjectMapper objectMapper,
            StarterSecurityErrorResponseFactory factory,
            SecurityProperties props) {
        return new StarterAccessDeniedHandler(factory, objectMapper, props.getErrors().getAccessDeniedMessage());
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(
            ObjectMapper objectMapper,
            StarterSecurityErrorResponseFactory factory,
            SecurityProperties props) {
        return new StarterAuthenticationEntryPoint(factory, objectMapper, props.getErrors().getUnauthorizedMessage());
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityProperties props,
            AccessDeniedHandler accessDeniedHandler,
            AuthenticationEntryPoint authenticationEntryPoint,
            StarterTokenFilter starterTokenFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource(props)))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(starterTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> {
            for (Rule rule : props.getRules()) {
                if (rule.getRole() != null && !rule.getRole().isEmpty()) {
                    auth.requestMatchers(rule.getEndpoint()).hasRole(rule.getRole());
                } else if (rule.isAuthenticated()) {
                    auth.requestMatchers(rule.getEndpoint()).authenticated();
                } else {
                    auth.requestMatchers(rule.getEndpoint()).permitAll();
                }
            }
            auth.anyRequest().authenticated();
        });

        try {
            SecurityFilterChain chain = http.build();
            log.info("DSS | SecurityFilterChain собран по параметрам: {}", props);
            return chain;
        } catch (Exception e) {
            log.error("DSS | Не удалось собрать SecurityFilterChain", e);
            throw e;
        }
    }

    private CorsConfigurationSource corsConfigurationSource(SecurityProperties props) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(props.getAllowedOrigins()); // конкретный источник
        configuration.setAllowedMethods(props.getAllowedMethods());
        configuration.setAllowedHeaders(props.getAllowedHeaders());
        configuration.setAllowCredentials(props.isAllowCredentials()); // нужно обязательно, если отправляются куки или токены
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
