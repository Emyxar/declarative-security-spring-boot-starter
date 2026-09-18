package ru.emyxar.security_starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.emyxar.security_starter.config.properties.Error;
import ru.emyxar.security_starter.config.properties.Jwt;
import ru.emyxar.security_starter.config.properties.Rule;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "security.config")
public class SecurityProperties {

    private List<Rule> rules = new ArrayList<>();
    private Jwt jwt = new Jwt();
    private Error errors = new Error();

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Error getErrors() {
        return errors;
    }

    public void setErrors(Error errors) {
        this.errors = errors;
    }
}
