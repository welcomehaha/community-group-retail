package com.community.security.filter;

import com.community.context.BaseContext;
import com.community.properties.JwtProperties;
import com.community.security.model.LoginUser;
import com.community.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 认证过滤器抽象基类。
 */
public abstract class AbstractJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    protected AbstractJwtAuthenticationFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (!supports(request) || isExcluded(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = request.getHeader(getTokenHeaderName());
            if (!StringUtils.hasText(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = JwtUtil.parseJWT(getSecretKey(), token);
            Long loginId = extractLoginId(claims);
            if (loginId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            LoginUser loginUser = LoginUser.builder()
                    .userId(loginId)
                    .userType(getUserType())
                    .permissions(Collections.emptyList())
                    .build();

            List<SimpleGrantedAuthority> authorities = Collections.emptyList();
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            BaseContext.setCurrentId(loginId);

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            BaseContext.removeCurrentId();
            filterChain.doFilter(request, response);
        } finally {
            BaseContext.removeCurrentId();
        }
    }

    /**
     * 判断当前请求是否由当前过滤器处理。
     *
     * @param request 请求对象
     * @return 是否处理
     */
    protected abstract boolean supports(HttpServletRequest request);

    /**
     * 排除无需认证的路径。
     *
     * @param request 请求对象
     * @return 是否排除
     */
    protected abstract boolean isExcluded(HttpServletRequest request);

    /**
     * 获取 Token 对应的请求头名称。
     *
     * @return 请求头名称
     */
    protected abstract String getTokenHeaderName();

    /**
     * 获取密钥。
     *
     * @return JWT密钥
     */
    protected abstract String getSecretKey();

    /**
     * 从 claims 中提取登录主体ID。
     *
     * @param claims claims信息
     * @return 登录ID
     */
    protected abstract Long extractLoginId(Claims claims);

    /**
     * 获取用户类型。
     *
     * @return 用户类型
     */
    protected abstract String getUserType();

    protected boolean match(String pattern, String path) {
        return antPathMatcher.match(pattern, path);
    }

    protected JwtProperties getJwtProperties() {
        return jwtProperties;
    }
}
