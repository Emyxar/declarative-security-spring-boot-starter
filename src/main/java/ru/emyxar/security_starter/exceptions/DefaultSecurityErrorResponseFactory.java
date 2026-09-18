package ru.emyxar.security_starter.exceptions;

import jakarta.servlet.http.HttpServletRequest;

public class DefaultSecurityErrorResponseFactory implements SecurityErrorResponseFactory{

    @Override
    public Object createErrorResponse(int status, String message, HttpServletRequest request, Exception exception) {
        return new DefaultSecurityError(status, message);
    }
}
