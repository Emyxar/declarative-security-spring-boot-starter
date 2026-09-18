package ru.emyxar.security_starter.exceptions;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface StarterSecurityErrorResponseFactory {

    Object createErrorResponse(String status,
                               String message,
                               HttpServletRequest request,
                               Exception exception);
}
