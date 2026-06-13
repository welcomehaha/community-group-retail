package com.community.service.impl;

import com.community.constant.StatusConstant;
import com.community.entity.RetailOrder;
import com.community.mapper.ProductMapper;
import com.community.mapper.RetailOrderMapper;
import com.community.mapper.ProductBundleMapper;
import com.community.mapper.UserMapper;
import com.community.service.WorkspaceService;
import com.community.vo.BusinessDataVO;
import com.community.vo.ProductOverViewVO;
import com.community.vo.OrderOverViewVO;
import com.community.vo.ProductBundleOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private RetailOrderMapper retailOrderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductBundleMapper productBundleMapper;

    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */

        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);

        //查询总订单数
        Integer totalOrderCount = retailOrderMapper.countByMap(map);

        map.put("status", RetailOrder.COMPLETED);
        //营业额
        Double turnover = retailOrderMapper.sumByMap(map);
        turnover = turnover == null? 0.0 : turnover;

        //有效订单数
        Integer validOrderCount = retailOrderMapper.countByMap(map);

        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0 && validOrderCount != 0){
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Integer newUsers = userMapper.countByMap(map);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }


    /**
     * 查询订单管理数据
     *
     * @return
     */
    public OrderOverViewVO getOrderOverView() {
        Map map = new HashMap();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
        map.put("status", RetailOrder.TO_BE_CONFIRMED);

        // 待履约接单
        Integer waitingRetailOrder = retailOrderMapper.countByMap(map);

        // 待履约配送
        map.put("status", RetailOrder.CONFIRMED);
        Integer deliveredRetailOrder = retailOrderMapper.countByMap(map);

        //已完成
        map.put("status", RetailOrder.COMPLETED);
        Integer completedRetailOrder = retailOrderMapper.countByMap(map);

        //已取消
        map.put("status", RetailOrder.CANCELLED);
        Integer cancelledRetailOrder = retailOrderMapper.countByMap(map);

        //全部订单
        map.put("status", null);
        Integer allRetailOrder = retailOrderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .waitingRetailOrder(waitingRetailOrder)
                .deliveredRetailOrder(deliveredRetailOrder)
                .completedRetailOrder(completedRetailOrder)
                .cancelledRetailOrder(cancelledRetailOrder)
                .allRetailOrder(allRetailOrder)
                .build();
    }

    /**
     * 查询商品总览
     *
     * @return
     */
    public ProductOverViewVO getProductOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = productMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = productMapper.countByMap(map);

        return ProductOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询组合商品总览
     *
     * @return
     */
    public ProductBundleOverViewVO getProductBundleOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = productBundleMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = productBundleMapper.countByMap(map);

        return ProductBundleOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
