package com.community.security.service;

import com.community.mapper.PermissionMapper;
import com.community.security.model.LoginUser;
import com.community.security.properties.PermissionProperties;
import com.community.security.util.SecurityContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 权限判断服务。
 *
 * <p>当前阶段优先走真实 RBAC 权限查询。
 * 对“未分配角色是否兼容放行”采用配置化治理，便于从兼容模式平滑切换到严格模式。</p>
 */
@Component("permissionService")
public class PermissionService {

    private final PermissionMapper permissionMapper;
    private final PermissionProperties permissionProperties;

    @Autowired
    public PermissionService(PermissionMapper permissionMapper, PermissionProperties permissionProperties) {
        this.permissionMapper = permissionMapper;
        this.permissionProperties = permissionProperties;
    }

    /**
     * 判断当前登录主体是否具备指定权限编码。
     *
     * @param authority 权限编码
     * @return 是否具备权限
     */
    public boolean hasAuthority(String authority) {
        if (!StringUtils.hasText(authority)) {
            return false;
        }

        LoginUser loginUser = SecurityContextUtil.getLoginUser();
        if (loginUser == null) {
            return false;
        }

        // 仅管理端主体参与后台权限判断。
        if (!"ADMIN".equals(loginUser.getUserType())) {
            return false;
        }

        Long staffId = loginUser.getUserId();
        if (staffId == null) {
            return false;
        }

        int roleCount = permissionMapper.countRolesByStaffId(staffId);
        if (roleCount <= 0) {
            // 兼容开关开启时允许未分配角色员工暂时访问，关闭后进入严格 RBAC 模式。
            return permissionProperties.isAllowAdminWithoutRole();
        }

        List<String> permissionCodes = permissionMapper.listPermissionCodesByStaffId(staffId);
        return permissionCodes.contains(authority);
    }
}
