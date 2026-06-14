package com.community.controller.user;

import com.community.dto.RetailOrderPaymentDTO;
import com.community.dto.RetailOrderSubmitDTO;
import com.community.result.PageResult;
import com.community.result.Result;
import com.community.service.RetailOrderService;
import com.community.vo.OrderPaymentVO;
import com.community.vo.OrderSubmitVO;
import com.community.vo.OrderVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Tag(name = "用户端订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private RetailOrderService retailOrderService;

    /**
     * 用户下单
     * @param retailOrderSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @Operation(summary = "用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody RetailOrderSubmitDTO retailOrderSubmitDTO){
        log.info("用户下单，参数为：{}",retailOrderSubmitDTO);
        OrderSubmitVO orderSubmitVO = retailOrderService.submitOrder(retailOrderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param retailOrderPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @Operation(summary = "订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody RetailOrderPaymentDTO retailOrderPaymentDTO) throws Exception {
        log.info("订单支付：{}", retailOrderPaymentDTO);
        OrderPaymentVO orderPaymentVO = retailOrderService.payment(retailOrderPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单查询
     *
     * @param page
     * @param pageSize
     * @param status   订单状态 1待付款 2待履约接单 3已接单 4履约中 5已完成 6已取消
     * @return
     */
    @GetMapping("/historyOrders")
    @Operation(summary = "历史订单查询")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        PageResult pageResult = retailOrderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = retailOrderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     *
     * @return
     */
    @PutMapping("/cancel/{id}")
    @Operation(summary = "取消订单")
    public Result cancel(@PathVariable("id") Long id) throws Exception {
        retailOrderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @Operation(summary = "再来一单")
    public Result repetition(@PathVariable Long id) {
        retailOrderService.repetition(id);
        return Result.success();
    }

    /**
     * 客户催单
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @Operation(summary = "客户催单")
    public Result reminder(@PathVariable("id") Long id){
        retailOrderService.reminder(id);
        return Result.success();
    }
}


