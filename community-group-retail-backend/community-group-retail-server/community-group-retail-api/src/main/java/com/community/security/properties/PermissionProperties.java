package com.community.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 权限兼容策略配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "community.security.permission")
public class PermissionProperties {

    /**
     * 未分配角色的管理端员工是否允许兼容放行。
     *
     * <p>默认开启，避免存量未分配角色员工立即被全部拒绝。
     * 待生产角色数据补齐后，应切换为 false，进入严格 RBAC 模式。</p>
     */
    private boolean allowAdminWithoutRole = true;
}
