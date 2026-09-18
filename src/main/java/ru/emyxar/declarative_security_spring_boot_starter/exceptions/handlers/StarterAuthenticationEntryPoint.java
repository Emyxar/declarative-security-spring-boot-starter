package ru.emyxar.declarative_security_spring_boot_starter.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.emyxar.declarative_security_spring_boot_starter.exceptions.StarterSecurityErrorResponseFactory;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class StarterAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final StarterSecurityErrorResponseFactory responseFactory;
    private final ObjectMapper objectMapper;
    private final String message;

    public StarterAuthenticationEntryPoint(StarterSecurityErrorResponseFactory responseFactory, ObjectMapper objectMapper, String message) {
        this.responseFactory = responseFactory;
        this.objectMapper = objectMapper;
        this.message = message;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Object body = responseFactory.createErrorResponse(
                "SC_UNAUTHORIZED",
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
