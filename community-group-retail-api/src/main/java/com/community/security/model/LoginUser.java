package com.community.security.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 安全上下文中的统一登录主体。
 */
@Data
@Builder
public class LoginUser implements Serializable {

    /**
     * 当前登录主体ID，管理端为员工ID，用户端为用户ID。
     */
    private Long userId;

    /**
     * 登录主体类型：ADMIN / USER。
     */
    private String userType;

    /**
     * 登录名或展示名，当前阶段可为空。
     */
    private String username;

    /**
     * 权限编码列表，当前骨架阶段先保留空集合，后续接 RBAC。
     */
    @Builder.Default
    private List<String> permissions = Collections.emptyList();
}
