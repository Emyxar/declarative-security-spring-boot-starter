package ru.emyxar.security_starter.exceptions.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import ru.emyxar.security_starter.exceptions.SecurityErrorResponseFactory;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

public class StarterAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorResponseFactory responseFactory;
    private final ObjectMapper objectMapper;
    private final String message;

    public StarterAccessDeniedHandler(SecurityErrorResponseFactory responseFactory, ObjectMapper objectMapper, String message) {
        this.responseFactory = responseFactory;
        this.objectMapper = objectMapper;
        this.message = message;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Object body = responseFactory.createErrorResponse(
                SC_FORBIDDEN,
                message,
                request,
                accessDeniedException);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
