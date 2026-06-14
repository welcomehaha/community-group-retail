package com.community.controller.admin;

import com.community.dto.RetailOrderCancelDTO;
import com.community.dto.RetailOrderConfirmDTO;
import com.community.dto.RetailOrderPageQueryDTO;
import com.community.dto.RetailOrderRejectionDTO;
import com.community.result.PageResult;
import com.community.result.Result;
import com.community.service.RetailOrderService;
import com.community.vo.OrderStatisticsVO;
import com.community.vo.OrderVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理
 */
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Tag(name = "订单管理接口")
public class OrderController {

    @Autowired
    private RetailOrderService retailOrderService;

    /**
     * 订单搜索
     *
     * @param retailOrderPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @Operation(summary = "订单搜索")
    @PreAuthorize("@permissionService.hasAuthority('order:manage:list')")
    public Result<PageResult> conditionSearch(RetailOrderPageQueryDTO retailOrderPageQueryDTO) {
        PageResult pageResult = retailOrderService.conditionSearch(retailOrderPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @GetMapping("/statistics")
    @Operation(summary = "各个状态的订单数量统计")
    @PreAuthorize("@permissionService.hasAuthority('order:manage:statistics')")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO orderStatisticsVO = retailOrderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @Operation(summary = "查询订单详情")
    @PreAuthorize("@permissionService.hasAuthority('order:manage:detail')")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = retailOrderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 履约接单
     *
     * @return
     */
    @PutMapping("/confirm")
    @Operation(summary = "履约接单")
    @PreAuthorize("@permissionService.hasAuthority('order:review:confirm')")
    public Result confirm(@RequestBody RetailOrderConfirmDTO retailOrderConfirmDTO) {
        retailOrderService.confirm(retailOrderConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     *
     * @return
     */
    @PutMapping("/rejection")
    @Operation(summary = "拒单")
    @PreAuthorize("@permissionService.hasAuthority('order:review:reject')")
    public Result rejection(@RequestBody RetailOrderRejectionDTO retailOrderRejectionDTO) throws Exception {
        retailOrderService.rejection(retailOrderRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     *
     * @return
     */
    @PutMapping("/cancel")
    @Operation(summary = "取消订单")
    @PreAuthorize("@permissionService.hasAuthority('order:review:cancel')")
    public Result cancel(@RequestBody RetailOrderCancelDTO retailOrderCancelDTO) throws Exception {
        retailOrderService.cancel(retailOrderCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     *
     * @return
     */
    @PutMapping("/delivery/{id}")
    @Operation(summary = "派送订单")
    @PreAuthorize("@permissionService.hasAuthority('order:review:delivery')")
    public Result delivery(@PathVariable("id") Long id) {
        retailOrderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    @Operation(summary = "完成订单")
    @PreAuthorize("@permissionService.hasAuthority('order:review:complete')")
    public Result complete(@PathVariable("id") Long id) {
        retailOrderService.complete(id);
        return Result.success();
    }
}


