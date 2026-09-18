package ru.emyxar.declarative_security_spring_boot_starter.exceptions;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface StarterSecurityErrorResponseFactory {

    Object createErrorResponse(String status,
                               String message,
                               HttpServletRequest request,
                               Exception exception);
}
