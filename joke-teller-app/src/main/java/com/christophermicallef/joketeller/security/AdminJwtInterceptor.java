package com.christophermicallef.joketeller.security;

import com.christophermicallef.joketeller.services.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

@Component
public class AdminJwtInterceptor implements HandlerInterceptor {
    public static final String AUTHENTICATED_USERNAME = AdminJwtInterceptor.class.getName() + ".username";
    public static final String AUTHENTICATED_USER_ID = AdminJwtInterceptor.class.getName() + ".userId";
    private final JwtTokenService tokens;
    private final AdminUserService users;

    public AdminJwtInterceptor(JwtTokenService tokens, AdminUserService users) {
        this.tokens = tokens;
        this.users = users;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (CorsUtils.isPreFlightRequest(request)) return true;

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            var claims = tokens.getClaims(authorization.substring(7).trim());
            if (claims.isPresent()) {
                var user = users.findActiveTokenUser(claims.get().userId(), claims.get().tokenVersion());
                if (user.isPresent()) {
                    request.setAttribute(AUTHENTICATED_USERNAME, user.get().getUsername());
                    request.setAttribute(AUTHENTICATED_USER_ID, user.get().getId());
                    return true;
                }
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"A valid bearer token is required\"}");
        return false;
    }
}
