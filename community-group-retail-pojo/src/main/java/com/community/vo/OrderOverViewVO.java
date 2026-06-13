package com.community.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单概览数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOverViewVO implements Serializable {
    //待接单数量
    private Integer waitingRetailOrder;

    //待派送数量
    private Integer deliveredRetailOrder;

    //已完成数量
    private Integer completedRetailOrder;

    //已取消数量
    private Integer cancelledRetailOrder;

    //全部订单
    private Integer allRetailOrder;
}
