package ru.emyxar.security_starter.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Rule {

    @NotBlank(message = "Эндпоинт не может быть пустым")
    private String endpoint;

    @Pattern(
            regexp = "^(?!ROLE_)[A-Z0-9_]+$",
            message = "Роль указывается с прфиксом ROLE_, например ROLE_ADMIN"
    )
    private String role;

    private boolean authenticated = false;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
