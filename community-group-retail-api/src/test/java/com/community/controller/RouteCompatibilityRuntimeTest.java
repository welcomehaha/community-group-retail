package com.community.controller;

import com.community.constant.JwtClaimsConstant;
import com.community.constant.StatusConstant;
import com.community.config.WebMvcConfiguration;
import com.community.controller.admin.BundleController;
import com.community.controller.admin.ProductController;
import com.community.controller.user.StoreController;
import com.community.properties.JwtProperties;
import com.community.security.config.SecurityConfig;
import com.community.security.filter.AdminJwtAuthenticationFilter;
import com.community.security.filter.UserJwtAuthenticationFilter;
import com.community.security.handler.RestAccessDeniedHandler;
import com.community.security.handler.RestAuthenticationEntryPoint;
import com.community.security.service.PermissionService;
import com.community.result.PageResult;
import com.community.service.ProductService;
import com.community.service.ProductBundleService;
import com.community.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        StoreController.class,
        com.community.controller.admin.StoreController.class,
        ProductController.class,
        BundleController.class
})
@Import({
        WebMvcConfiguration.class,
        SecurityConfig.class,
        AdminJwtAuthenticationFilter.class,
        UserJwtAuthenticationFilter.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class
})
class RouteCompatibilityRuntimeTest {

    private static final String ADMIN_TOKEN_NAME = "token";
    private static final String USER_TOKEN_NAME = "authentication";
    private static final String ADMIN_SECRET = "test-admin-secret-key-community-retail-2026-secure";
    private static final String USER_SECRET = "test-user-secret-key-community-retail-2026-secure";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisTemplate redisTemplate;

    @MockBean
    private ValueOperations valueOperations;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductBundleService productBundleService;

    @MockBean
    private JwtProperties jwtProperties;

    @MockBean(name = "permissionService")
    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(jwtProperties.getAdminTokenName()).thenReturn(ADMIN_TOKEN_NAME);
        when(jwtProperties.getAdminSecretKey()).thenReturn(ADMIN_SECRET);
        when(jwtProperties.getAdminTtl()).thenReturn(3600000L);
        when(jwtProperties.getUserTokenName()).thenReturn(USER_TOKEN_NAME);
        when(jwtProperties.getUserSecretKey()).thenReturn(USER_SECRET);
        when(jwtProperties.getUserTtl()).thenReturn(3600000L);
        when(permissionService.hasAuthority(any())).thenReturn(true);
    }

    @Test
    void userStoreStatusShouldBeAccessibleWithoutLogin() throws Exception {
        when(valueOperations.get(StoreController.KEY)).thenReturn(StatusConstant.DISABLE);

        mockMvc.perform(get("/user/store/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(StatusConstant.DISABLE));
    }

    @Test
    void userShopStatusShouldBeAccessibleWithoutLogin() throws Exception {
        when(valueOperations.get(StoreController.KEY)).thenReturn(StatusConstant.ENABLE);

        mockMvc.perform(get("/user/shop/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(StatusConstant.ENABLE));
    }

    @Test
    void adminProductCompatibilityRouteShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/admin/product/page"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminLegacyProductCompatibilityRouteShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/admin/dish/page"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminProductCompatibilityRouteShouldServeWithValidAuthentication() throws Exception {
        when(productService.pageQuery(any()))
                .thenReturn(new PageResult(0, Collections.emptyList()));

        mockMvc.perform(get("/admin/product/page")
                        .header(ADMIN_TOKEN_NAME, createAdminToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void adminLegacyProductCompatibilityRouteShouldServeWithValidAuthentication() throws Exception {
        when(productService.pageQuery(any()))
                .thenReturn(new PageResult(0, Collections.emptyList()));

        mockMvc.perform(get("/admin/dish/page")
                        .header(ADMIN_TOKEN_NAME, createAdminToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void adminBundleCompatibilityRouteShouldServeWithValidAuthentication() throws Exception {
        when(productBundleService.pageQuery(any()))
                .thenReturn(new PageResult(0, Collections.emptyList()));

        mockMvc.perform(get("/admin/bundle/page")
                        .header(ADMIN_TOKEN_NAME, createAdminToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void adminLegacyProductBundleCompatibilityRouteShouldServeWithValidAuthentication() throws Exception {
        when(productBundleService.pageQuery(any()))
                .thenReturn(new PageResult(0, Collections.emptyList()));

        mockMvc.perform(get("/admin/setmeal/page")
                        .header(ADMIN_TOKEN_NAME, createAdminToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void adminStoreCompatibilityRouteShouldServeWithValidAuthentication() throws Exception {
        when(valueOperations.get(com.community.controller.admin.StoreController.KEY)).thenReturn(StatusConstant.DISABLE);

        mockMvc.perform(get("/admin/store/status")
                        .header(ADMIN_TOKEN_NAME, createAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(StatusConstant.DISABLE));
    }

    @Test
    void adminLegacyShopCompatibilityRouteShouldServeWithValidAuthentication() throws Exception {
        when(valueOperations.get(com.community.controller.admin.StoreController.KEY)).thenReturn(StatusConstant.ENABLE);

        mockMvc.perform(get("/admin/shop/status")
                        .header(ADMIN_TOKEN_NAME, createAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(StatusConstant.ENABLE));
    }

    private static String createAdminToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, 1L);
        return JwtUtil.createJWT(ADMIN_SECRET, 3600000, claims);
    }
}
