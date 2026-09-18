package ru.emyxar.declarative_security_spring_boot_starter.exceptions.internal;

import jakarta.servlet.http.HttpServletRequest;
import ru.emyxar.declarative_security_spring_boot_starter.exceptions.StarterSecurityErrorResponseFactory;

public class DefaultSecurityErrorResponse implements StarterSecurityErrorResponseFactory {

    @Override
    public Object createErrorResponse(String status, String message, HttpServletRequest request, Exception exception) {
        return new DefaultSecurityError(status, message);
    }
}
