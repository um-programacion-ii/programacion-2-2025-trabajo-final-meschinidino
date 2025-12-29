package com.eventos.proxy.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class ServiceAuthInterceptor implements HandlerInterceptor {

    private final ServiceJwtProvider serviceJwtProvider;

    public ServiceAuthInterceptor(ServiceJwtProvider serviceJwtProvider) {
        this.serviceJwtProvider = serviceJwtProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return reject(response);
        }

        String token = auth.substring("Bearer ".length());
        if (!serviceJwtProvider.isValid(token)) {
            return reject(response);
        }

        return true;
    }

    private boolean reject(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"unauthorized\"}");
        return false;
    }
}
