package ru.emyxar.security_starter.exceptions.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.emyxar.security_starter.exceptions.SecurityErrorResponseFactory;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

public class StarterAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorResponseFactory responseFactory;
    private final ObjectMapper objectMapper;
    private final String message;

    public StarterAuthenticationEntryPoint(SecurityErrorResponseFactory responseFactory, ObjectMapper objectMapper, String message) {
        this.responseFactory = responseFactory;
        this.objectMapper = objectMapper;
        this.message = message;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Object body = responseFactory.createErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                message,
                request,
                authException
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
