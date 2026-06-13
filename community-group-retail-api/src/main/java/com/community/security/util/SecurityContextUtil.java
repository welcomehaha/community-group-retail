package com.community.security.util;

import com.community.context.BaseContext;
import com.community.security.model.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类。
 */
public final class SecurityContextUtil {

    private SecurityContextUtil() {
    }

    /**
     * 获取当前登录主体。
     *
     * @return 登录主体，不存在时返回null
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser loginUser) {
            return loginUser;
        }
        return null;
    }

    /**
     * 获取当前登录ID，兼容 BaseContext。
     *
     * @return 当前登录ID
     */
    public static Long getCurrentId() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getUserId();
        }
        return BaseContext.getCurrentId();
    }
}
