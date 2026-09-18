package ru.emyxar.declarative_security_spring_boot_starter.properties;

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

    @Override
    public String toString() {
        return "Error{" +
                "accessDeniedMessage='" + accessDeniedMessage + '\'' +
                ", unauthorizedMessage='" + unauthorizedMessage + '\'' +
                '}';
    }
}
