package com.community.controller.admin;

import com.community.result.Result;
import com.community.service.WorkspaceService;
import com.community.vo.BusinessDataVO;
import com.community.vo.ProductOverViewVO;
import com.community.vo.OrderOverViewVO;
import com.community.vo.ProductBundleOverViewVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 工作台
 */
@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Tag(name = "工作台相关接口")
public class WorkSpaceController {

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 工作台今日数据查询
     * @return
     */
    @GetMapping("/businessData")
    @Operation(summary = "工作台今日数据查询")
    @PreAuthorize("@permissionService.hasAuthority('dashboard:workspace:business-data')")
    public Result<BusinessDataVO> businessData(){
        //获得当天的开始时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        //获得当天的结束时间
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);
        return Result.success(businessDataVO);
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @GetMapping("/overviewOrders")
    @Operation(summary = "查询订单管理数据")
    @PreAuthorize("@permissionService.hasAuthority('dashboard:workspace:order-overview')")
    public Result<OrderOverViewVO> orderOverView(){
        return Result.success(workspaceService.getOrderOverView());
    }

    /**
     * 查询商品总览
     * @return
     */
    @GetMapping("/overviewProductes")
    @Operation(summary = "查询商品总览")
    @PreAuthorize("@permissionService.hasAuthority('dashboard:workspace:product-overview')")
    public Result<ProductOverViewVO> dishOverView(){
        return Result.success(workspaceService.getProductOverView());
    }

    /**
     * 查询组合商品总览
     * @return
     */
    @GetMapping("/overviewProductBundles")
    @Operation(summary = "查询组合商品总览")
    @PreAuthorize("@permissionService.hasAuthority('dashboard:workspace:bundle-overview')")
    public Result<ProductBundleOverViewVO> setmealOverView(){
        return Result.success(workspaceService.getProductBundleOverView());
    }
}


