package com.community.security.service;

import com.community.mapper.PermissionMapper;
import com.community.security.model.LoginUser;
import com.community.security.properties.PermissionProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * PermissionService 单元测试。
 */
class PermissionServiceTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowAdminWithoutRoleWhenCompatibilityEnabled() {
        PermissionMapper permissionMapper = mock(PermissionMapper.class);
        PermissionProperties permissionProperties = new PermissionProperties();
        permissionProperties.setAllowAdminWithoutRole(true);
        PermissionService permissionService = new PermissionService(permissionMapper, permissionProperties);

        mockAdminLoginUser(1L);
        when(permissionMapper.countRolesByStaffId(1L)).thenReturn(0);

        assertTrue(permissionService.hasAuthority("report:statistics:turnover"));
    }

    @Test
    void shouldDenyAdminWithoutRoleWhenCompatibilityDisabled() {
        PermissionMapper permissionMapper = mock(PermissionMapper.class);
        PermissionProperties permissionProperties = new PermissionProperties();
        permissionProperties.setAllowAdminWithoutRole(false);
        PermissionService permissionService = new PermissionService(permissionMapper, permissionProperties);

        mockAdminLoginUser(2L);
        when(permissionMapper.countRolesByStaffId(2L)).thenReturn(0);

        assertFalse(permissionService.hasAuthority("report:statistics:turnover"));
    }

    @Test
    void shouldCheckPermissionCodesWhenRoleExists() {
        PermissionMapper permissionMapper = mock(PermissionMapper.class);
        PermissionProperties permissionProperties = new PermissionProperties();
        permissionProperties.setAllowAdminWithoutRole(false);
        PermissionService permissionService = new PermissionService(permissionMapper, permissionProperties);

        mockAdminLoginUser(3L);
        when(permissionMapper.countRolesByStaffId(3L)).thenReturn(1);
        when(permissionMapper.listPermissionCodesByStaffId(3L))
                .thenReturn(List.of("dashboard:workspace:business-data", "report:statistics:turnover"));

        assertTrue(permissionService.hasAuthority("report:statistics:turnover"));
        assertFalse(permissionService.hasAuthority("report:statistics:top10"));
    }

    private void mockAdminLoginUser(Long userId) {
        LoginUser loginUser = LoginUser.builder()
                .userId(userId)
                .userType("ADMIN")
                .username("tester")
                .build();
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
