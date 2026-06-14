package com.community.controller;

import com.community.controller.admin.ProductController;
import com.community.controller.user.BundleController;
import com.community.controller.user.StoreController;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteCompatibilityAnnotationTest {

    @Test
    void adminProductControllerShouldKeepNewAndLegacyRootPaths() {
        RequestMapping requestMapping = ProductController.class.getAnnotation(RequestMapping.class);

        assertContains(requestMapping.value(), "/admin/product");
        assertContains(requestMapping.value(), "/admin/dish");
    }

    @Test
    void userBundleControllerShouldKeepNewAndLegacyRootPaths() {
        RequestMapping requestMapping = BundleController.class.getAnnotation(RequestMapping.class);

        assertContains(requestMapping.value(), "/user/bundle");
        assertContains(requestMapping.value(), "/user/setmeal");
    }

    @Test
    void userBundleDetailShouldKeepNewAndLegacyChildPaths() throws NoSuchMethodException {
        Method productListMethod = BundleController.class.getMethod("productList", Long.class);
        GetMapping getMapping = productListMethod.getAnnotation(GetMapping.class);

        assertContains(getMapping.value(), "/product/{id}");
        assertContains(getMapping.value(), "/dish/{id}");
    }

    @Test
    void userProductControllerShouldKeepNewAndLegacyRootPaths() {
        RequestMapping requestMapping = com.community.controller.user.ProductController.class.getAnnotation(RequestMapping.class);

        assertContains(requestMapping.value(), "/user/product");
        assertContains(requestMapping.value(), "/user/dish");
    }

    @Test
    void userStoreControllerShouldKeepNewAndLegacyRootPaths() {
        RequestMapping requestMapping = StoreController.class.getAnnotation(RequestMapping.class);

        assertContains(requestMapping.value(), "/user/store");
        assertContains(requestMapping.value(), "/user/shop");
    }

    private static void assertContains(String[] values, String expected) {
        assertTrue(Arrays.asList(values).contains(expected), "missing mapping: " + expected);
    }
}
