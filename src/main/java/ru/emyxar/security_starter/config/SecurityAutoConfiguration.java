package ru.emyxar.security_starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import ru.emyxar.security_starter.config.properties.Rule;
import ru.emyxar.security_starter.exceptions.DefaultSecurityErrorResponseFactory;
import ru.emyxar.security_starter.exceptions.SecurityErrorResponseFactory;
import ru.emyxar.security_starter.exceptions.handlers.StarterAccessDeniedHandler;
import ru.emyxar.security_starter.exceptions.handlers.StarterAuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SecurityErrorResponseFactory.class)
    public SecurityErrorResponseFactory securityErrorResponseFactory() {
        return new DefaultSecurityErrorResponseFactory();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(
            ObjectMapper objectMapper,
            SecurityErrorResponseFactory factory,
            SecurityProperties props) {
        return new StarterAccessDeniedHandler(factory, objectMapper, props.getErrors().getAccessDeniedMessage());
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(
            ObjectMapper objectMapper,
            SecurityErrorResponseFactory factory,
            SecurityProperties props) {
        return new StarterAuthenticationEntryPoint(factory, objectMapper, props.getErrors().getUnauthorizedMessage());
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityProperties props,
            AccessDeniedHandler accessDeniedHandler,
            AuthenticationEntryPoint authenticationEntryPoint) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint));

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

        return http.build();
    }
}
