package ru.emyxar.security_starter.properties;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "security.config")
public class SecurityProperties {

    @PostConstruct
    void logValidationLoaded() {
        System.out.println("Security starter properties loaded");
    }

    @Valid
    private List<Rule> rules = new ArrayList<>();

    @Valid
    private Error errors = new Error();

    @Valid
    private List<String> allowedOrigins = new ArrayList<>();

    @Valid
    private List<String> allowedMethods = new ArrayList<>();

    @Valid
    private List<String> allowedHeaders = new ArrayList<>();

    @Valid
    private boolean allowCredentials = false;

    @AssertTrue(message = """
            security.config.allowed-origins не может содержать '*',
            когда security.config.allow-credentials=true
            """)
    public boolean isCorsConfigurationValid() {
        return !allowCredentials || !allowedOrigins.contains("*");
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public Error getErrors() {
        return errors;
    }

    public void setErrors(Error errors) {
        this.errors = errors;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }
}
