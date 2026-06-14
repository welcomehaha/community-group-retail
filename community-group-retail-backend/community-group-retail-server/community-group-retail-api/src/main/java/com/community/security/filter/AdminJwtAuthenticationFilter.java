package com.community.security.filter;

import com.community.constant.JwtClaimsConstant;
import com.community.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 管理端 JWT 认证过滤器。
 */
@Component
public class AdminJwtAuthenticationFilter extends AbstractJwtAuthenticationFilter {

    public AdminJwtAuthenticationFilter(JwtProperties jwtProperties) {
        super(jwtProperties);
    }

    @Override
    protected boolean supports(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/admin/");
    }

    @Override
    protected boolean isExcluded(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return match("/admin/employee/login", uri);
    }

    @Override
    protected String getTokenHeaderName() {
        return getJwtProperties().getAdminTokenName();
    }

    @Override
    protected String getSecretKey() {
        return getJwtProperties().getAdminSecretKey();
    }

    @Override
    protected Long extractLoginId(Claims claims) {
        Object value = claims.get(JwtClaimsConstant.EMP_ID);
        return value == null ? null : Long.valueOf(value.toString());
    }

    @Override
    protected String getUserType() {
        return "ADMIN";
    }
}
