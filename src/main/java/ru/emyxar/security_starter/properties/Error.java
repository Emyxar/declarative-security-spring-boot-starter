package ru.emyxar.security_starter.properties;

public class Error {

    private String accessDeniedMessage = "Доступ запрещён";
    private String unauthorizedMessage = "Требуется аутентификация";

    public String getAccessDeniedMessage() {
        return accessDeniedMessage;
    }

    public void setAccessDeniedMessage(String accessDeniedMessage) {
        this.accessDeniedMessage = accessDeniedMessage;
    }

    public String getUnauthorizedMessage() {
        return unauthorizedMessage;
    }

    public void setUnauthorizedMessage(String unauthorizedMessage) {
        this.unauthorizedMessage = unauthorizedMessage;
    }
}
