package com.christophermicallef.joketeller.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

@Component
public class AdminJwtInterceptor implements HandlerInterceptor {
    public static final String AUTHENTICATED_USERNAME = AdminJwtInterceptor.class.getName() + ".username";
    private final JwtTokenService tokens;

    public AdminJwtInterceptor(JwtTokenService tokens) { this.tokens = tokens; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (CorsUtils.isPreFlightRequest(request)) return true;

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            var username = tokens.getUsername(authorization.substring(7).trim());
            if (username.isPresent()) {
                request.setAttribute(AUTHENTICATED_USERNAME, username.get());
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"A valid bearer token is required\"}");
        return false;
    }
}
