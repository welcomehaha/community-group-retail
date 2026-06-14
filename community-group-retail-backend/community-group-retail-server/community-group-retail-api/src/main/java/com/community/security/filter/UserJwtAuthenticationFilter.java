package com.community.security.filter;

import com.community.constant.JwtClaimsConstant;
import com.community.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 用户端 JWT 认证过滤器。
 */
@Component
public class UserJwtAuthenticationFilter extends AbstractJwtAuthenticationFilter {

    public UserJwtAuthenticationFilter(JwtProperties jwtProperties) {
        super(jwtProperties);
    }

    @Override
    protected boolean supports(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/user/");
    }

    @Override
    protected boolean isExcluded(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return match("/user/user/login", uri)
                || match("/user/store/status", uri)
                || match("/user/shop/status", uri);
    }

    @Override
    protected String getTokenHeaderName() {
        return getJwtProperties().getUserTokenName();
    }

    @Override
    protected String getSecretKey() {
        return getJwtProperties().getUserSecretKey();
    }

    @Override
    protected Long extractLoginId(Claims claims) {
        Object value = claims.get(JwtClaimsConstant.USER_ID);
        return value == null ? null : Long.valueOf(value.toString());
    }

    @Override
    protected String getUserType() {
        return "USER";
    }
}
