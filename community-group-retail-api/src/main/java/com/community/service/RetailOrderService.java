package com.community.service;

import com.community.dto.*;
import com.community.result.PageResult;
import com.community.vo.OrderPaymentVO;
import com.community.vo.OrderStatisticsVO;
import com.community.vo.OrderSubmitVO;
import com.community.vo.OrderVO;

public interface RetailOrderService {
    /**
     * 用户下单
     * @param retailOrderSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(RetailOrderSubmitDTO retailOrderSubmitDTO);

    /**
     * 订单支付
     * @param retailOrderPaymentDTO
     * @return
     */
    OrderPaymentVO payment(RetailOrderPaymentDTO retailOrderPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 用户端订单分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 用户取消订单
     * @param id
     */
    void userCancelById(Long id) throws Exception;

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 条件搜索订单
     * @param retailOrderPageQueryDTO
     * @return
     */
    PageResult conditionSearch(RetailOrderPageQueryDTO retailOrderPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     *
     * @param retailOrderConfirmDTO
     */
    void confirm(RetailOrderConfirmDTO retailOrderConfirmDTO);

    /**
     * 拒单
     *
     * @param retailOrderRejectionDTO
     */
    void rejection(RetailOrderRejectionDTO retailOrderRejectionDTO) throws Exception;

    /**
     * 商家取消订单
     *
     * @param retailOrderCancelDTO
     */
    void cancel(RetailOrderCancelDTO retailOrderCancelDTO) throws Exception;

    /**
     * 派送订单
     *
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);

    /**
     * 客户催单
     * @param id
     */
    void reminder(Long id);
}
