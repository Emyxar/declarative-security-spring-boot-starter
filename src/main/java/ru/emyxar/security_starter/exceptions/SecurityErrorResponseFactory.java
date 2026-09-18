package ru.emyxar.security_starter.exceptions;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface SecurityErrorResponseFactory {

    Object createErrorResponse(int status,
                               String message,
                               HttpServletRequest request,
                               Exception exception);
}
